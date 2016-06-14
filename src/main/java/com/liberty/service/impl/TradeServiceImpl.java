package com.liberty.service.impl;

import com.liberty.common.FifaRequests;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.Comparators.getAuctionInfoComparator;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:11
 */
@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

  public static final int DEFAULT_LOW_BOUND = 1000;
  private static int MAX_PURCHASE_AMOUNT = 40;
  private static int MIN_DELAY = 2000;
  private static int MAX_DELAY = 5000;
  private FifaRequests fifaRequests = new FifaRequests();
  private boolean failed;
  private int purchases = 0;

  @Autowired
  private PlayerTradeStatusRepository tradeRepository;

  @Autowired
  private PlayerStatisticRepository statisticRepository;


  @Override
  public void removeAllPlayers() {
    tradeRepository.deleteAll();
  }

  @Override
  public void checkMarket() {
    if (failed) {
      onFailed();
      return;
    }
    if (purchases >= MAX_PURCHASE_AMOUNT) {
      log.info(
          "MAX purchase amount is : " + MAX_PURCHASE_AMOUNT + " currently bought " + purchases);
      return;
    }
    List<PlayerTradeStatus> players = tradeRepository.findAll();
    Collections.shuffle(players, new Random(System.currentTimeMillis()));
    log.info("Monitor : " + players.size() + " players");
    if (isEmpty(players)) {
      log.info("Nothing to buy. Player trade is empty");
      return;
    }
    for (PlayerTradeStatus p : players) {
      log.info("Trying to check " + p.getName() + " max price => " + p.getMaxPrice());
      boolean success = checkMarket(p);
      if (!success) {
        failed = false;
        break;
      }
      log.info("Total purchases : " + purchases);
      if (purchases >= MAX_PURCHASE_AMOUNT) {
        log.info("Limit of purchases : " + purchases);
        failed = true;
        onFailed();
        return;
      }
      sleep();
    }
  }

  private void onFailed() {
    log.debug("Previously failed");
    Toolkit.getDefaultToolkit().beep();
    Toolkit.getDefaultToolkit().beep();
    Toolkit.getDefaultToolkit().beep();
  }

  private void sleep() {
    int delay = new Random().nextInt(MAX_DELAY - MIN_DELAY) + MIN_DELAY;
    log.info("Waiting " + delay + " millis");
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }

  private boolean checkMarket(PlayerTradeStatus playerTradeStatus) {
    try {
      sleep();
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerTradeStatus.getId(),
          playerTradeStatus.getMaxPrice());
      if (!maybe.isPresent()) {
        return false;
      }
      TradeStatus tradeStatus = maybe.get();
      int found = tradeStatus.getAuctionInfo().size();
      log.info("Found " + found + " players for " + playerTradeStatus.getName() + " maxPrice : "
          + playerTradeStatus.getMaxPrice());
      if (found <= 0) {
        return true;
      }
      buyPlayers(tradeStatus, playerTradeStatus);
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public void addToAutoBuy(String name, long id, int maxPrice) {
    PlayerTradeStatus status = new PlayerTradeStatus(id, 0, name, maxPrice, null);
    tradeRepository.save(status);
  }

  public void sell() {
    fifaRequests.item();
    fifaRequests.auctionHouse();
  }

  private Optional<AuctionInfo> foundMin(TradeStatus tradeStatus) {
    Optional<AuctionInfo> min = tradeStatus.getAuctionInfo().stream()
        .min(getAuctionInfoComparator());
    return min.flatMap(m -> {
      if (m.getItemData().getContract() <= 0) {
        return Optional.empty();
      }
      return Optional.of(m);
    });
  }

  private List<AuctionInfo> foundMinList(TradeStatus tradeStatus) {
    return tradeStatus.getAuctionInfo().stream()
        .sorted(getAuctionInfoComparator().reversed())
        .filter(a -> a.getItemData().getContract() > 0)
        .collect(Collectors.toList());
  }


  private void buyPlayers(TradeStatus tradeStatus, PlayerTradeStatus playerTradeStatus) {

    List<AuctionInfo> list = foundMinList(tradeStatus);
    if (list.isEmpty()) {
      log.error("Can not find trades for " + playerTradeStatus.getName());
      return;
    }
    for (AuctionInfo info : list) {
      boolean success = buyOne(info, playerTradeStatus);
      if (success && purchases >= MAX_PURCHASE_AMOUNT) {
        onFailed();
        return;
      }
      if (!success) {
        return;
      }
    }
  }

  private boolean buyOne(AuctionInfo auctionInfo, PlayerTradeStatus playerTradeStatus) {
    log.info("Found min player : " + auctionInfo.getBuyNowPrice());
    boolean success = fifaRequests.buy(auctionInfo);
    if (!success) {
      failed = false;
      return success;
    }
    log.info(
        "Success bought " + playerTradeStatus.getName() + " for " + auctionInfo.getBuyNowPrice());
    purchases++;
    PlayerTradeStatus one = tradeRepository.findOne(playerTradeStatus.getId());
    if (one == null) {
      log.error("Player with id " + auctionInfo.getItemData().getId() + " not found. Player name " +
          ": " + auctionInfo.getItemData().getAssetId());
      return true;
    }
    one.setBoughtAmount(one.getBoughtAmount() + 1);
    tradeRepository.save(one);
    return true;
  }


  public void findMinPrice(long playerId) {
    PlayerStatistic player = statisticRepository.findOne(playerId);
    if (player == null) {
      player = new PlayerStatistic();
    }

    Integer lowBound = player.getLastPrice();
    if (lowBound == null) {
      lowBound = DEFAULT_LOW_BOUND;
    }


  }
}
