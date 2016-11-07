package com.liberty.robot;

import com.liberty.common.BidState;
import com.liberty.common.BoundHelper;
import com.liberty.common.DelayHelper;
import com.liberty.common.ErrorCode;
import com.liberty.common.TradeState;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.TradeInfo;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.SearchService;
import com.liberty.service.TradeService;
import com.liberty.service.TransactionService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.DateHelper.getDurationString;

/**
 * User: Dimitr
 * Date: 15.10.2016
 * Time: 14:14
 */
@Component
@Slf4j
public class AuctionRobot {

  private static final int PAGES_TO_SEARCH = 50;

  private static final int BID_LIMIT = 20;
  private static final int WIN_ITEMS_LIMIT = 20;
  private static final int MAX_EXPIRATION_TIME = 300;
  private int wonItems;
  private boolean disabled = false;

  @Autowired
  private TradeService tradeService;

  @Autowired
  private SearchService searchService;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private LogController logController;

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Autowired
  private TransactionService transactionService;

  private RobotBidStrategy bidStrategy = new RobotBidStrategy.CheapPlayers();

  @Scheduled(fixedRate = 100_000)
  public void findBids() {
    if (disabled) {
      logController.info("Auction Robot disabled");
      return;
    }
    log.debug("Robot trying to find applicable bids");
    if (wonItems > WIN_ITEMS_LIMIT) {
      log.debug("Robot findBids exceeded limit of won items");
      return;
    }
    int bids = 0;
    Watchlist watchlist = tradeService.getWatchlist();
    Integer currentCredits = watchlist.getCredits();
    if (currentCredits == null || currentCredits < 1000) {
      return;
    }
    List<PlayerTradeStatus> toSearch = tradeStatusRepository.findAll().stream()
        .filter(PlayerTradeStatus::isEnabled)
        .collect(Collectors.toList());
    log.info("Robot trying to find any from " + toSearch.size() + " players");
    Map<Long, PlayerTradeStatus> statusMap = toSearch.stream()
        .collect(Collectors.toMap(PlayerTradeStatus::getId, Function.identity()));
    for (int i = 0; i < PAGES_TO_SEARCH; i++) {
      List<TradeInfo> trades = getPage(i);
      bids += findApplicableTrades(trades, statusMap);
      if (bids > BID_LIMIT) {
        log.debug("Robot exceeded bid limit. Current bids : " + bids);
        return;
      }
      if (disabled) {
        log.info("Auction robot is disabled");
        return;
      }
      logPageProcessed(i + 1, trades);
      if (exceededExpirationTime(trades.get(trades.size() - 1))) {
        log.info("Exceeded max expiration time");
        break;
      }
      DelayHelper.wait(300, 10);
    }
  }

  private boolean exceededExpirationTime(TradeInfo tradeInfo) {
    return tradeInfo.getAuctionInfo().getExpires() > MAX_EXPIRATION_TIME;
  }

  private void logPageProcessed(int page, List<TradeInfo> trades) {
    log.info("Robot processed " + page + " pages");
    if (page % 10 == 0) {
      logController.info("Auction Bot processed : " + page + " pages");
    }
    if (page % PAGES_TO_SEARCH == 0) {
      logController
          .info("Last item expiration time : " + getDurationString(trades.get(trades.size() - 1)
              .getAuctionInfo().getExpires()));
    }
  }

  @Scheduled(fixedRate = 10_000)
  public void checkWatchlist() {
    if (disabled) {
      log.info("Auction Robot disabled");
      return;
    }
    log.debug("Trying to run robot trade");
    List<AuctionInfo> targets = tradeService.getTransferTargets();
    if (targets.size() > 0) {
      logController.info("Found " + targets.size() + " transfer targets...");
    }
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
    if (targets.size() > 0) {
      log.debug("Trying to process " + active.size() + " active targets...");
    }
    wonItems = won.size();
    if (wonItems > WIN_ITEMS_LIMIT) {
      logController.info("Robot exceeded limit of won items");
      return;
    }
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

  private int findApplicableTrades(List<TradeInfo> trades, Map<Long, PlayerTradeStatus> statuses) {
    int found = 0;
    for (TradeInfo trade : trades) {
      AuctionInfo auctionInfo = trade.getAuctionInfo();
      Long playerId = auctionInfo.getItemData().getAssetId();
      PlayerTradeStatus tradeStatus = statuses.get(playerId);
      if (tradeStatus == null) {
        continue;
      }
      Integer maxPrice = tradeStatus.getMaxPrice();
      if (shouldBid(auctionInfo, maxPrice)) {
        log.info("Robot found applicable item to bid : " + tradeStatus.getName() +
            " for " + defineBid(auctionInfo));
        if (processItem(auctionInfo)) {
          found++;
        }
        DelayHelper.wait(60, 5);
      }
    }
    return found;
  }

  private boolean shouldBid(AuctionInfo info, Integer maxPrice) {
    try {

      long bid = defineBid(info);
      if (maxPrice == null) {
        System.err.println("Max price can not be null for : " + info.getItemData().getName());
        return false;
      }
      return bid <= maxPrice && info.getExpires() <= MAX_EXPIRATION_TIME && (info.getItemData()
          .getContract() > 0 || bid + 1000 <= maxPrice);
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
  }

  private List<TradeInfo> getPage(int page) {
    MarketSearchRequest searchRequest = bidStrategy.buildRequest(page);
    return searchService.search(searchRequest);
  }

  private void processWonItems(List<AuctionInfo> won) {
    won.forEach(i -> transactionService.logBuyByRobot(i));

    logController.info("You have won " + won.size() + " items");
  }

  private synchronized boolean processItem(AuctionInfo info) {
    log.debug("Trying to process " + info.getTradeId() + " trade");
    Long playerId = info.getItemData().getAssetId();
    if (playerId == null) {
      log.error("Bad item : " + info.getTradeId());
      return false;
    }

    PlayerTradeStatus tradeStatus = getPlayerTrade(playerId);
    if (tradeStatus == null) {
      log.error("Can not trade " + playerId + " player. There is no PlayerTradeStatus item.");
      return false;
    }
    long nextBid = defineBid(info);
    if (nextBid <= tradeStatus.getMaxPrice()) {
      if (!isMyBid(info)) {
        BidStatus bidStatus = tradeService.makeBid(info.getTradeId(), nextBid);
        if (bidStatus.getStatus() == BidStatus.Status.FAIL &&
            bidStatus.getErrorCode() == ErrorCode.NOT_ALLOWED) {
          makeHigherBid(info);
        }
        log.info("Robot made bid for " + tradeStatus.getName() +
            ". Status : " + bidStatus.getStatus());
        return true;
      } else {
        log.info("My bid for " + tradeStatus.getName() + " is highest");
      }
    } else {
      tradeService.removeFromTargets(info.getTradeId());
    }
    return false;
  }

  private int defineBid(AuctionInfo info) {
    Integer currentBid = info.getCurrentBid();
    if (currentBid == null || currentBid == 0) {
      return info.getStartingBid();
    }
    return BoundHelper.defineNextBid(currentBid);
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

  public boolean isDisabled() {
    return disabled;
  }


  public void setEnabled(Boolean enabled) {
    if (enabled != null) {
      disabled = !enabled;
    }
  }
}
