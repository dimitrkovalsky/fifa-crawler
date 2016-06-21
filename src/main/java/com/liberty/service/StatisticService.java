package com.liberty.service;

import com.liberty.model.Statistic;
import com.liberty.model.market.AuctionInfo;

import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
public interface StatisticService {

  Statistic getGeneralStatistic();

  void collectStatistic(long playerId, int lowBound, Set<AuctionInfo> toStatistic);
}
