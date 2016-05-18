package com.liberty.service.impl;

import com.liberty.model.PlayerMonitoring;
import com.liberty.model.PlayerProfile;
import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerMonitoringRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.HistoryService;
import com.liberty.service.MonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonitoringServiceImpl implements MonitoringService {

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private PlayerInfoRepository infoRepository;

  @Autowired
  private CrawlerService crawlerService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private PlayerMonitoringRepository monitoringRepository;

  @Override
  public void monitor(long playerId) {
    PlayerProfile profile = updateInfo(playerId);
    PlayerMonitoring monitoring = new PlayerMonitoring();
    monitoring.setId(playerId);           // TODO: check monitoring existence
    monitoring.setStartPrice(profile.getPrice());
    monitoringRepository.save(monitoring);
    historyService.recordHistory(profile);
  }

  private PlayerProfile updateInfo(long playerId) {
//    if(!profileRepository.exists(playerId))
    return crawlerService.fetchData(playerId);
  }
}
