package com.liberty.service.impl;

import com.liberty.common.FifaRequests;
import com.liberty.model.MarketInfo;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
  public static final int STATISTIC_PLAYER_COLLECTION_AMOUNT = 20;
  private int maxPurchaseAmount = 40;
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
    if (purchases >= maxPurchaseAmount) {
      log.info(
          "MAX purchase amount is : " + maxPurchaseAmount + " currently bought " + purchases);
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
      if (purchases >= maxPurchaseAmount) {
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

  private Optional<AuctionInfo> foundMin(List<AuctionInfo> statuses) {
    Optional<AuctionInfo> min = statuses.stream()
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
      if (success && purchases >= maxPurchaseAmount) {
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

  @Override
  public PlayerStatistic findMinPrice(long playerId) {
    PlayerStatistic player = statisticRepository.findOne(playerId);
    if (player == null) {
      player = new PlayerStatistic();
      player.setId(playerId);
    }

    Integer lowBound = player.getLastPrice();
    if (lowBound == null) {
      lowBound = DEFAULT_LOW_BOUND;
    }
    int iteration = 0;
    List<AuctionInfo> toStatistic = new ArrayList<>();

    while (toStatistic.size() < STATISTIC_PLAYER_COLLECTION_AMOUNT) {
      iteration++;
      List<AuctionInfo> players = findPlayers(playerId, lowBound);
      if (players.size() == 0) {
        lowBound = getHigherBound(0, lowBound);
      } else if (players.size() >= 20) {
        lowBound = getLowerBound(lowBound);
      } else {
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      }

      if (iteration >= 20) {
        log.info("Exceeded iteration limit");
      }
    }
    if (toStatistic.isEmpty()) {
      PlayerStatistic toSave = new PlayerStatistic();
      toSave.setId(playerId);
      toSave.setLastPrice(lowBound);
      statisticRepository.save(toSave);
    }
    Map<Integer, List<AuctionInfo>> stats =
        toStatistic.stream().collect(Collectors.groupingBy(AuctionInfo::getBuyNowPrice));
    updateStats(playerId, toStatistic, stats);


    log.info("Found " + toStatistic.size() + " players in " + iteration + " iterations");
    log.info(stats.toString());
    return getMinPrice(playerId);
  }

  private void updateStats(long playerId, List<AuctionInfo> toStatistic,
                           Map<Integer, List<AuctionInfo>> stats) {
    foundMin(toStatistic).ifPresent(m -> {
      PlayerStatistic toSave = new PlayerStatistic();
      toSave.setId(playerId);
      toSave.setLastPrice(m.getBuyNowPrice());
      List<PlayerStatistic.PriceDistribution> prices = toStatistic(stats);
      toSave.setPrices(prices);
      System.out.println("Player stats : " + toSave);
      statisticRepository.save(toSave);
    });
  }

  private List<PlayerStatistic.PriceDistribution> toStatistic(
      Map<Integer, List<AuctionInfo>> stats) {
    List<PlayerStatistic.PriceDistribution> result = new ArrayList<>();
    stats.forEach((k, v) -> {
      result.add(new PlayerStatistic.PriceDistribution(k, v.size()));
    });
    return result;
  }

  private Integer getLowerBound(Integer lowBound) {
    if (lowBound <= 1000) {
      return lowBound - 50;
    } else if (lowBound <= 10000) {
      return lowBound - 100;
    } else if (lowBound <= 50000) {
      return lowBound - 250;
    } else if (lowBound <= 100000) {
      return lowBound - 500;
    } else {
      return lowBound - 1000;
    }

  }

  public List<AuctionInfo> findPlayers(long playerId, Integer lowBound) {
    try {
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerId, lowBound);
      return maybe.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
    } catch (Exception e) {
      log.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  private int getHigherBound(int found, Integer lowBound) {
    if (lowBound < 1000) {
      return lowBound + 50;
    } else if (lowBound < 10000) {
      return lowBound + 100;
    } else if (lowBound < 50000) {
      return lowBound + 250;
    } else if (lowBound < 100000) {
      return lowBound + 500;
    } else {
      return lowBound + 1000;
    }
  }

  @Override
  public MarketInfo getMarketInfo() {
    MarketInfo info = new MarketInfo();
    info.setMaxPurchases(maxPurchaseAmount);
    info.setPhishingToken(fifaRequests.getPhishingToken());
    info.setSessionId(fifaRequests.getSessionId());
    return info;
  }

  @Override
  public void setMarketInfo(MarketInfo info) {
//    this.maxPurchaseAmount = info.getMaxPurchases();
    fifaRequests.setPhishingToken(info.getPhishingToken());
    fifaRequests.setSessionId(info.getSessionId());
  }

  @Override
  public void autoBuy(boolean run) {

  }

  @Override
  public void deleteFromAutoBuy(Long id) {
    statisticRepository.delete(id);
  }

  @Override
  public PlayerStatistic getMinPrice(Long id) {
    return statisticRepository.findOne(id);
  }

  @Override
  public List<PlayerTradeStatus> getAllToAutoBuy() {
    return tradeRepository.findAll();
  }

  @Override
  public PlayerTradeStatus getOnePlayer(Long id) {
    return tradeRepository.findOne(id);
  }
}
