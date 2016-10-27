package com.liberty.service.impl;

import com.liberty.common.BoundHelper;
import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.PriceService;
import com.liberty.service.RequestService;
import com.liberty.service.StatisticService;
import com.liberty.service.TransactionService;
import com.liberty.websockets.LogController;

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
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.BoundHelper.defineLowBound;
import static com.liberty.common.BoundHelper.getHigherBound;
import static com.liberty.service.impl.TradeServiceImpl.ITERATION_LIMIT;
import static com.liberty.service.impl.TradeServiceImpl.STATISTIC_PLAYER_COLLECTION_AMOUNT;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
@Service
@Slf4j
public class PriceServiceImpl implements PriceService {

  @Autowired
  protected LogController logController;

  @Autowired
  protected TransactionService transactionService;

  @Autowired
  protected RequestService requestService;

  @Autowired
  protected PlayerTradeStatusRepository tradeRepository;

  @Autowired
  private PlayerProfileRepository playerProfileService;


  @Autowired
  private PlayerStatisticRepository statisticRepository;

  @Autowired
  private StatisticService statisticService;

  @Override
  public void findMinPriceAll() {
    List<PlayerTradeStatus> all = tradeRepository.findAll();
    Collections.sort(all, Comparator.comparingLong(PlayerTradeStatus::getMaxPrice));

    final int[] counter = {0};
    all.forEach(p -> {
      findMinPrice(p.getId());
      counter[0]++;
      logController.info("Updated market price for " + counter[0] + " / " + all.size());
      DelayHelper.wait(17000, 1000);
    });
  }

  @Override
  public void updatePriceDistribution(boolean enabled) {
    List<PlayerTradeStatus> beforeFilter = tradeRepository.findAll();
    List<PlayerTradeStatus> all;
    if (enabled) {
      all = beforeFilter.stream().filter(PlayerTradeStatus::isEnabled).collect(Collectors.toList());
    } else {
      all = beforeFilter;
    }

    Collections.sort(all, Comparator.comparingLong(PlayerTradeStatus::getMaxPrice));

    final int[] counter = {0};
    all.forEach(p -> {
      findMinPrice(p.getId());
      counter[0]++;
      logController.info("Updated price distribution for " + counter[0] + " / " + all.size());
      DelayHelper.wait(17000, 1000);
    });
  }

  @Override
  public PlayerStatistic findMinPrice(long playerId) {
    PlayerStatistic playerStatistic = statisticRepository.findOne(playerId);
    PlayerTradeStatus tradeStatus = tradeRepository.findOne(playerId);
    PlayerProfile profile = playerProfileService.findOne(playerId);
    if (playerStatistic == null) {
      playerStatistic = new PlayerStatistic();
      playerStatistic.setId(playerId);
    }
    if (tradeStatus == null) {
      tradeStatus = createNewTrade(profile);
    }
    Integer lowBound = defineLowBound(playerStatistic, tradeStatus);

    int iteration = 0;
    Set<AuctionInfo> toStatistic = new HashSet<>();

    while (toStatistic.size() < STATISTIC_PLAYER_COLLECTION_AMOUNT && !isMaxBound(profile)) {
      iteration++;
      logController.info("Trying to find " + tradeStatus.getName() + " less than " + lowBound);
      DelayHelper.wait(250, 20);
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

  private boolean isMaxBound(PlayerProfile profile) {
    return false;
  }


  private PlayerTradeStatus createNewTrade(PlayerProfile profile) {
    PlayerTradeStatus tradeStatus = new PlayerTradeStatus(profile.getId(), profile.getName(),
        BoundHelper.defineMaxBuyNow(profile));
    tradeStatus.setEnabled(false);
    tradeRepository.save(tradeStatus);
    tradeStatus.updateDate();
    return tradeStatus;
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
      Optional<TradeStatus> maybe = requestService.searchPlayer(playerId, lowBound, page);
      return maybe.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
    } catch (Exception e) {
      logController.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public PlayerStatistic getMinPrice(Long id) {
    return statisticRepository.findOne(id);
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
          p.updateDate();
          return p;
        }).forEach(tradeRepository::save);
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
}
