package com.liberty.robot;

import com.liberty.common.BidState;
import com.liberty.common.BoundHelper;
import com.liberty.common.DelayHelper;
import com.liberty.common.ErrorCode;
import com.liberty.common.TradeState;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr
 * Date: 15.10.2016
 * Time: 14:14
 */
@Component
@Slf4j
public class AuctionRobot {

  @Autowired
  private TradeService tradeService;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Scheduled(fixedRate = 10_000)
  public void trade() {
    log.debug("Trying to run robot trade");
    List<AuctionInfo> targets = tradeService.getTransferTargets();
    log.debug("Found " + targets.size() + " transfer targets...");
    List<AuctionInfo> expired = new ArrayList<>();
    List<AuctionInfo> won = new ArrayList<>();
    List<AuctionInfo> active = new ArrayList<>();
    for (AuctionInfo info : targets) {
      if (info.getTradeState().equals(TradeState.CLOSED) &&
          info.getBidState().equals(BidState.HIGHEST)) {
        won.add(info);
      }
      if (info.getTradeState().equals(TradeState.CLOSED)) {
        expired.add(info);
      }
      if (info.getTradeState().equals(TradeState.ACTIVE)) {
        active.add(info);
      }
    }
    log.debug("Trying to process " + active.size() + " active targets...");
    active.forEach(x -> {
      processItem(x);
      DelayHelper.waitStrict(33);
    });

    if (!expired.isEmpty()) {
      tradeService.removeExpired(expired);
    }
    if (!won.isEmpty()) {
      processWonItems(won);
    }
    DelayHelper.wait(2000);
  }

  private void processWonItems(List<AuctionInfo> won) {
    log.info("You have " + won.size() + " won items");
  }

  private void processItem(AuctionInfo info) {
    log.debug("Trying to process " + info.getTradeId() + " trade");
    Long playerId = info.getItemData().getAssetId();
    Integer currentBid = info.getCurrentBid();
//    PlayerProfile profile = getProfile(playerId);
    if(playerId == null){
      int i = 0;
      log.debug("Id null");
    }
    PlayerTradeStatus tradeStatus = getPlayerTrade(playerId);
    if (tradeStatus == null) {
      log.error("Can not trade " + playerId + " player. There is no PlayerTradeStatus item.");
      return;
    }
    int nextBid = BoundHelper.defineNextBid(currentBid);
    if (nextBid <= tradeStatus.getMaxPrice()) {
      if (!isMyBid(info)) {
        BidStatus bidStatus = tradeService.makeBid(info.getTradeId(), nextBid);
        if (bidStatus.getStatus() == BidStatus.Status.FAIL &&
            bidStatus.getErrorCode() == ErrorCode.NOT_ALLOWED) {
          makeHigherBid(info);
        }
        log.info("Robot made bid for " + tradeStatus.getName() +
            ". Status : " + bidStatus.getStatus());
      } else {
        log.info("My bid for " + tradeStatus.getName() + " is highest");
      }
    } else {
      tradeService.removeFromTargets(info.getTradeId());
    }
  }

  private void makeHigherBid(AuctionInfo info) {
    TradeStatus status = tradeService.getTradeStatus(info.getTradeId());
    if (status.getCode() == null && !CollectionUtils.isEmpty(status.getAuctionInfo())) {
      processItem(status.getAuctionInfo().get(0));
    } else {
      log.error("Error makeHigherBid. status : " + status);
    }
  }

  private boolean isMyBid(AuctionInfo info) {
    return info.getBidState().equals(BidState.HIGHEST);
  }

  private PlayerTradeStatus getPlayerTrade(Long playerId) {
    return tradeStatusRepository.findOne(playerId);
  }

  private PlayerProfile getProfile(Long playerId) {
    return profileRepository.findOne(playerId);
  }
}
