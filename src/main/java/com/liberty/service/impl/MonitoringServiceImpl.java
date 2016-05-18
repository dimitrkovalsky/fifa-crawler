package com.liberty.service.impl;

import com.liberty.repositories.PlayerInfoRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.CrawlerService;
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

  @Override
  public void monitor(long playerId) {
     checkExistence(playerId);
  }

  private void checkExistence(long playerId) {
    if(!profileRepository.exists(playerId))
      crawlerService.fetchData(playerId);
  }
}
