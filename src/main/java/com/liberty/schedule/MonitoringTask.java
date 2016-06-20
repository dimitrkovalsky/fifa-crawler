package com.liberty.schedule;

import com.liberty.service.MonitoringService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
//@Component
@Slf4j
public class MonitoringTask {

  @Autowired
  private LogController logController;

  @Autowired
  private MonitoringService monitoringService;

  @Scheduled(fixedRate = 300000)
  public void monitor() {
    log.info("Trying to update prices");
    monitoringService.updatePrices(logController::onPriceChanged);
  }

}
