package com.liberty.service.impl;

import com.liberty.model.Statistic;
import com.liberty.repositories.PlayerMonitoringRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.SourceRepository;
import com.liberty.service.StatisticService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@Service
public class StatisticServiceImpl implements StatisticService {

  @Autowired
  private PlayerProfileRepository playerProfileRepository;
  @Autowired
  private PlayerMonitoringRepository playerMonitoringRepository;
  @Autowired
  private SourceRepository sourceRepository;

  @Override
  public Statistic getGeneralStatistic() {
    Statistic statistic = new Statistic();
    statistic.setProfiles(playerProfileRepository.count());
    statistic.setMonitored(playerMonitoringRepository.count());
    statistic.setSources(sourceRepository.count());
    return statistic;
  }
}
