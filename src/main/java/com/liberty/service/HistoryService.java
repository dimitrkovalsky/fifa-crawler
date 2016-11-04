package com.liberty.service;

import com.liberty.common.PriceHelper;
import com.liberty.model.PlayerStatistic;

import java.util.List;
import java.util.Map;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
public interface HistoryService {

  void logPriceChange(Long playerId, List<PlayerStatistic.PriceDistribution> prices);

  void logPriceChange(Long playerId, Map<Integer, Integer> priceDistribution);

  void logPriceChange(PlayerStatistic statistic);

  Map<Long, PriceHelper.HistoryPoint> getHistoryGraph(Long playerId);
}
