package com.liberty.service.impl;

import com.liberty.model.PlayerStatistic;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Service
public class HistoryServiceImpl implements HistoryService {

  @Autowired
  private PriceHistoryRepository historyRepository;

  @Override
  public void logPriceChange(Long playerId, List<PlayerStatistic.PriceDistribution> prices) {
    Map<Integer, Integer> priceDistribution = prices.stream()
        .collect(Collectors.toMap(PlayerStatistic.PriceDistribution::getPrice,
            PlayerStatistic.PriceDistribution::getAmount));
    logPriceChange(playerId, priceDistribution);
  }

  @Override
  public void logPriceChange(Long playerId, Map<Integer, Integer> priceDistribution) {
    PriceHistory priceHistory = historyRepository.findOne(playerId);
    if (priceHistory == null) {
      priceHistory = new PriceHistory();
      priceHistory.setPlayerId(playerId);
    }
    priceHistory.getHistory().put(System.currentTimeMillis(), priceDistribution);
    historyRepository.save(priceHistory);
  }

  @Override
  public void logPriceChange(PlayerStatistic statistic) {

    logPriceChange(statistic.getId(), statistic.getPrices());
  }
}
