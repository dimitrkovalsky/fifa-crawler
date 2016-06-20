package com.liberty.service.impl;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.Statistic;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@Service
public class StatisticServiceImpl implements StatisticService {

  @Autowired
  private PlayerTradeStatusRepository statusRepository;

  @Autowired
  private TradeService tradeService;


  @Override
  public Statistic getGeneralStatistic() {
    Statistic statistic = new Statistic();
    statistic.setPlayers(statusRepository.count());
    statistic.setAutoBuy(statusRepository.findAll().stream().filter(PlayerTradeStatus::isEnabled).count());
    statistic.setEnabled(tradeService.getMarketInfo().getAutoBuyEnabled());
    return statistic;
  }
}
