package com.liberty.service.impl;

import com.liberty.model.PlayerStatistic;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.HistoryService;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Override
  public Map<Long, HistoryPoint> getHistoryGraph(Long playerId) {
    PriceHistory history = historyRepository.findOne(playerId);
    if (history == null) {
      history = new PriceHistory();
      history.setPlayerId(playerId);
      historyRepository.save(history);
    }

    Set<Long> dates = history.getHistory().keySet();
    Map<Long, Map<Integer, Integer>> historyMap = history.getHistory();

    SortedMap<Long, HistoryPoint> collectedHistory = new TreeMap<>();
    dates.forEach(d -> {
          HistoryPoint point = buildHistory(historyMap.get(d));
          collectedHistory.put(d, point);
        }
    );
    return collectedHistory;
  }

  private HistoryPoint buildHistory(Map<Integer, Integer> priceDistribution) {
    DescriptiveStatistics stats = new DescriptiveStatistics();
    priceDistribution.forEach((k, v) -> {
      for (int i = 0; i < v; i++) {
        stats.addValue(k);
      }
    });

    return new HistoryPoint((long) stats.getMin(), (long) stats.getPercentile(50));
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class HistoryPoint {

    private Long minPrice;
    private Long median;
  }
}
