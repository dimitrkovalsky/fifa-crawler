package com.liberty.service.impl;

import com.liberty.model.PlayerMonitoring;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerMonitoringRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.HistoryService;
import com.liberty.service.MonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    PlayerMonitoring one = monitoringRepository.findOne(playerId);
    if (one != null) {
      log.error("[MONITORING]", "Player with id " + playerId + " under monitoring");
      return;
    }
    PlayerProfile profile = updateInfo(playerId);
    PlayerMonitoring monitoring = new PlayerMonitoring();
    monitoring.setId(playerId);
    monitoring.setStartPrice(profile.getPrice());
    monitoringRepository.save(monitoring);
    historyService.recordHistory(profile);
  }

  private PlayerProfile updateInfo(long playerId) {
//    if(!profileRepository.exists(playerId))
    return crawlerService.fetchData(playerId);
  }

  @Override
  public void updatePrices() {
    monitoringRepository.findAll().stream().forEach(p -> {
      Price currentPrice = getCurrentPrice(p.getId());
      historyService.recordHistory(p.getId(), currentPrice);
    });
  }

  private Price getCurrentPrice(Long id) {
    return crawlerService.getCurrentPrice(id);
  }
}
