package com.liberty.service.impl;

import com.liberty.common.FifaRequests;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.liberty.common.Comparators.getAuctionInfoComparator;

/**
 * User: Dimitr Date: 18.06.2016 Time: 13:09
 */
@org.springframework.stereotype.Component
public abstract class ATradeService {

  protected int maxPurchaseAmount = 40;
  protected static int MIN_DELAY = 2000;
  protected static int MAX_DELAY = 5000;
  protected int purchases = 0;
  protected boolean failed;

  @Autowired
  protected LogController logController;

  @Autowired
  protected PlayerTradeStatusRepository tradeRepository;

  public List<PlayerTradeStatus> search(String phrase) {
    return tradeRepository.findByName(phrase)
        .stream().limit(5)
        .collect(Collectors.toList());
  }

  protected FifaRequests fifaRequests = new FifaRequests();

  protected void buyPlayers(TradeStatus tradeStatus, PlayerTradeStatus playerTradeStatus) {

    List<AuctionInfo> list = foundMinList(tradeStatus);
    if (list.isEmpty()) {
      logController.error("Can not find trades for " + playerTradeStatus.getName());
      return;
    }
    for (AuctionInfo info : list) {
      boolean success = buyOne(info, playerTradeStatus);
      if (success && purchases >= maxPurchaseAmount) {
        onFailed();
        return;
      }
      if (!success) {
        return;
      }
    }
  }

  protected boolean buyOne(AuctionInfo auctionInfo, PlayerTradeStatus playerTradeStatus) {
    logController.info("Found min player : " + auctionInfo.getBuyNowPrice());
    boolean success = fifaRequests.buy(auctionInfo);
    if (!success) {
      failed = false;
      return success;
    }
    logController.info(
        "Success bought " + playerTradeStatus.getName() + " for " + auctionInfo.getBuyNowPrice());
    purchases++;
    PlayerTradeStatus one = tradeRepository.findOne(playerTradeStatus.getId());
    if (one == null) {
      logController.error(
          "Player with id " + auctionInfo.getItemData().getId() + " not found. Player name " +
              ": " + auctionInfo.getItemData().getAssetId());
      return true;
    }
    one.setBoughtAmount(one.getBoughtAmount() + 1);
    tradeRepository.save(one);
    return true;
  }

  protected void sleep() {
    sleep(0);
  }

  protected void sleep(int millis) {
    int delay = new Random().nextInt(MAX_DELAY - MIN_DELAY) + MIN_DELAY + millis;
    logController.info("Waiting " + delay + " millis");
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      logController.error(e.getMessage());
    }
  }

  protected void onFailed() {
    logController.error("Previously failed");
    Toolkit.getDefaultToolkit().beep();
    Toolkit.getDefaultToolkit().beep();
    Toolkit.getDefaultToolkit().beep();
  }

  private List<AuctionInfo> foundMinList(TradeStatus tradeStatus) {
    return tradeStatus.getAuctionInfo().stream()
        .sorted(getAuctionInfoComparator().reversed())
        .filter(a -> a.getItemData().getContract() > 0)
        .collect(Collectors.toList());
  }


}
