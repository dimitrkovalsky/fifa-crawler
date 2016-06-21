package com.liberty.service.impl;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.rest.request.BuyRequest;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  public static final int STATISTIC_PLAYER_COLLECTION_AMOUNT = 25;
  public static final int ITERATION_LIMIT = 20;

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
              p.setMaxPrice(price - 200);
            } else if (price <= 2000) {
              p.setMaxPrice(price - 300);
            } else if (price <= 3000) {
              p.setMaxPrice(price - 400);
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
          playerTradeStatus.getMaxPrice());
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
    if (player.getPrices().size() >= 10) {
      return null;
    }
    Integer lowBound = defineLowBound(player, tradeStatus);

    int iteration = 0;
    Set<AuctionInfo> toStatistic = new HashSet<>();

    while (toStatistic.size() < STATISTIC_PLAYER_COLLECTION_AMOUNT) {
      iteration++;
      logController.info("Trying to find " + tradeStatus.getName() + " less than " + lowBound);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      List<AuctionInfo> players = findPlayers(playerId, lowBound);
      if (players.size() == 0) {
        lowBound = getHigherBound(0, lowBound);
      } else {
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      }
      logController.info("Found " + players.size() + " players");

      if (iteration >= ITERATION_LIMIT) {
        logController.info("Exceeded iteration limit");
        break;
      }
    }

    statisticService.collectStatistic(playerId, lowBound, toStatistic);

    logController.info("Found " + toStatistic.size() + " players in " + iteration + " iterations");
    return getMinPrice(playerId);
  }


  private List<AuctionInfo> findPlayers(long playerId, Integer lowBound) {
    try {
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerId, lowBound);
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
    info.setPhishingToken(fifaRequests.getPhishingToken());
    info.setSessionId(fifaRequests.getSessionId());
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
  public void autoBuy(boolean run) {
    this.autoBuyEnabled = run;
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

    Map<Long, Integer> idMinPrice = getMinPricesMap();
    all.forEach(p -> {
      Integer price = idMinPrice.get(p.getId());
      if (price != null) {
        p.setMinMarketPrice(price);
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
}
