package com.liberty.service.impl;

import com.liberty.common.UrlResolver;
import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.liberty.common.BoundHelper.defineLowBound;
import static com.liberty.common.BoundHelper.getHigherBound;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:11
 */
@Service
public class TradeServiceImpl extends ASellService implements TradeService {

  public static final int DEFAULT_LOW_BOUND = 1000;
  public static final int STATISTIC_PLAYER_COLLECTION_AMOUNT = 15;
  public static final int ITERATION_LIMIT = 35;

  private boolean autoBuyEnabled = true;

  @Autowired
  private PlayerStatisticRepository statisticRepository;

  @Autowired
  private StatisticService statisticService;

  @Override
  public void removeAllPlayers() {
    tradeRepository.deleteAll();
  }

  @Override
  public void updatePrices() {
    Map<Long, Integer> pricesMap = getMinPricesMap();
    tradeRepository.findAll().stream()
        .map(p -> {
          p.setEnabled(false);
          Integer price = pricesMap.get(p.getId());
          if (price != null && price != 0) {
            p.setEnabled(true);
            if (price <= 1000) {
              p.setMaxPrice(price - 100);
            } else if (price <= 2000) {
              p.setMaxPrice(price - 200);
            } else if (price <= 3000) {
              p.setMaxPrice(price - 300);
            } else if (price <= 4000) {
              p.setMaxPrice(price - 500);
            } else if (price <= 5000) {
              p.setMaxPrice(price - 700);
            } else {
              p.setEnabled(false);
            }
          }
          return p;
        }).forEach(tradeRepository::save);
  }

  @Override
  public void checkMarket() {
    if (!autoBuyEnabled || failed) {
      logController.info("Auto Buy Disabled...");
      return;
    }
    if (purchases >= maxPurchaseAmount) {
      logController.info(
          "MAX purchase amount is : " + maxPurchaseAmount + " currently bought " + purchases);
      return;
    }
    List<PlayerTradeStatus> players = tradeRepository.findAll().stream()
        .filter(PlayerTradeStatus::isEnabled)
        .collect(Collectors.toList());
    Collections.shuffle(players, new Random(System.currentTimeMillis()));
    logController.info("Monitor : " + players.size() + " players");
    if (isEmpty(players)) {
      logController.info("Nothing to buy. Player trade is empty");
      return;
    }
    for (PlayerTradeStatus p : players) {
      logController.info("Trying to check " + p.getName() + " max price => " + p.getMaxPrice());
      if (!autoBuyEnabled) {
        return;
      }
      boolean success = checkMarket(p);
      if (!success) {
        failed = false;
        break;
      }
      logController.info("Total purchases : " + purchases);
      if (purchases >= maxPurchaseAmount) {
        logController.info("Limit of purchases : " + purchases);
        failed = true;
        onFailed();
        return;
      }
      sleep();
    }
  }

  private boolean checkMarket(PlayerTradeStatus playerTradeStatus) {
    try {
      sleep();
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerTradeStatus.getId(),
          playerTradeStatus.getMaxPrice(), 0);
      if (!maybe.isPresent()) {
        return false;
      }
      TradeStatus tradeStatus = maybe.get();
      int found = tradeStatus.getAuctionInfo().size();
      logController
          .info("Found " + found + " players for " + playerTradeStatus.getName() + " maxPrice : "
              + playerTradeStatus.getMaxPrice());
      if (found <= 0) {
        return true;
      }
      buyPlayers(tradeStatus, playerTradeStatus);
    } catch (Exception e) {
      logController.error(e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public void addToAutoBuy(String name, long id, int maxPrice) {
    PlayerTradeStatus status = new PlayerTradeStatus(id, name, maxPrice);
    tradeRepository.save(status);
  }

  @Override
  public void findMinPriceAll() {
    List<PlayerTradeStatus> all = tradeRepository.findAll();
    Collections.sort(all, Comparator.comparingLong(PlayerTradeStatus::getMaxPrice));

    final int[] counter = {0};
    all.stream().forEach(p -> {
      findMinPrice(p.getId());
      counter[0]++;
      logController.info("Updated market price for " + counter[0] + " / " + all.size());
      sleep(20000);
    });
  }

  @Override
  public PlayerStatistic findMinPrice(long playerId) {
    PlayerStatistic player = statisticRepository.findOne(playerId);
    PlayerTradeStatus tradeStatus = tradeRepository.findOne(playerId);
    if (player == null) {
      player = new PlayerStatistic();
      player.setId(playerId);
    }
//    if (player.getPrices().size() >= 10) {
//      return null;
//    }
    Integer lowBound = defineLowBound(player, tradeStatus);

    int iteration = 0;
    Set<AuctionInfo> toStatistic = new HashSet<>();

    while (toStatistic.size() < STATISTIC_PLAYER_COLLECTION_AMOUNT && lowBound < 11000) {
      iteration++;
      logController.info("Trying to find " + tradeStatus.getName() + " less than " + lowBound);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      List<AuctionInfo> players = findPlayers(playerId, lowBound, 0);
      if (players.size() == 0) {
        lowBound = getHigherBound(0, lowBound);
      } else if (players.size() >= 12) {
        players.addAll(findNextPagesPlayers(playerId, lowBound));
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      } else {
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      }
      logController.info("Found " + toStatistic.size() + " players");

      if (iteration >= ITERATION_LIMIT) {
        logController.info("Exceeded iteration limit");
        break;
      }
    }

    statisticService.collectStatistic(playerId, lowBound, toStatistic);

    logController.info("Found " + toStatistic.size() + " players in " + iteration + " iterations");
    return getMinPrice(playerId);
  }

  private List<AuctionInfo> findNextPagesPlayers(long playerId, Integer lowBound) {
    boolean completed = false;
    int page = 1;
    List<AuctionInfo> players = new ArrayList<>();
    while (!completed) {
      List<AuctionInfo> found = findPlayers(playerId, lowBound, page * 12);
      if (found.size() < 12) {
        completed = true;
      } else {
        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      players.addAll(found);
      page++;
    }
    return players;
  }

  private List<AuctionInfo> findPlayers(long playerId, Integer lowBound, int page) {
    try {
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerId, lowBound, page);
      return maybe.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
    } catch (Exception e) {
      logController.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public MarketInfo getMarketInfo() {
    MarketInfo info = new MarketInfo();
    info.setMaxPurchases(maxPurchaseAmount);
    info.setPhishingToken(fifaRequests.getPhishingTokenForCheck());
    info.setSessionId(fifaRequests.getSessionForCheck());
    info.setAutoBuyEnabled(autoBuyEnabled);
    return info;
  }

  @Override
  public void setMarketInfo(MarketInfo info) {
//    this.maxPurchaseAmount = info.getMaxPurchases();
    fifaRequests.setPhishingToken(info.getPhishingToken());
    fifaRequests.setSessionId(info.getSessionId());
  }

  @Override
  public void updateTokens(String sessionId, String phishingToken, Boolean external) {
    if (external != null) {
      UrlResolver.externalUrl = external;
    }
    if (!phishingToken.equals(fifaRequests.getPhishingTokenForCheck())) {
      fifaRequests.setPhishingToken(phishingToken);
      logController.info("Updated phishingToken to " + phishingToken);
    }
    if (!sessionId.equals(fifaRequests.getSessionForCheck())) {
      fifaRequests.setSessionId(sessionId);
      logController.info("Updated sessionId to " + sessionId);
    }
  }

  @Override
  public void autoBuy(AutobuyRequest request) {
    this.autoBuyEnabled = request.getEnabled();
    if (autoBuyEnabled) {
      purchases = 0;
      failed = false;
      maxPurchaseAmount = request.getPurchases();
      checkMarket();
    }
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
    List<PlayerTradeStatus> all = tradeRepository.findAll();

    Map<Long, PlayerStatistic> idMinPrice = getStatsMap();
    all.forEach(p -> {
      PlayerStatistic stats = idMinPrice.get(p.getId());
      if (stats != null) {
        List<PlayerStatistic.PriceDistribution> prices = stats.getPrices();
        if (prices != null && !prices.isEmpty()) {
          p.setMinMarketPrice(prices.get(0).getPrice());
        }
        p.setLastUpdate(stats.getDate());
      }
    });
    return all;
  }

  private Map<Long, Integer> getMinPricesMap() {
    List<PlayerStatistic> stats = statisticRepository.findAll();
    Map<Long, Integer> idMinPrice = new HashMap<>();
    stats.forEach(s -> {
      if (!s.getPrices().isEmpty()) {
        idMinPrice.put(s.getId(), s.getPrices().get(0).getPrice());
      }
    });
    return idMinPrice;
  }

  private Map<Long, PlayerStatistic> getStatsMap() {
    List<PlayerStatistic> stats = statisticRepository.findAll();
    Map<Long, PlayerStatistic> idMinStats = new HashMap<>();
    stats.forEach(s -> {
      idMinStats.put(s.getId(), s);
    });
    return idMinStats;
  }

  @Override
  public PlayerTradeStatus getOnePlayer(Long id) {
    return tradeRepository.findOne(id);
  }

  @Override
  public void updateAutoBuy(BuyRequest request) {
    PlayerTradeStatus playerTradeStatus = tradeRepository.findOne(request.getId());
    playerTradeStatus.setEnabled(request.getEnabled());
    tradeRepository.save(playerTradeStatus);
  }

  @Override
  public void updatePlayer(PlayerTradeStatus request) {
    PlayerTradeStatus toUpdate = tradeRepository.findOne(request.getId());
    toUpdate.setSellStartPrice(request.getSellStartPrice());
    toUpdate.setSellBuyNowPrice(request.getSellBuyNowPrice());
    toUpdate.setMaxPrice(request.getMaxPrice());
    toUpdate.setName(request.getName());

    tradeRepository.save(toUpdate);
  }

  @Override
  protected Integer getPurchasesRemained() {
    return maxPurchaseAmount - purchases;
  }
}
