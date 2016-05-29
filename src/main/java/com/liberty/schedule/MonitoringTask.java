package com.liberty.schedule;

import com.liberty.service.MonitoringService;
import com.liberty.websockets.LiveController;

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
  private LiveController liveController;

  @Autowired
  private MonitoringService monitoringService;

  @Scheduled(fixedRate = 60000)
  public void monitor() {
    log.info("Trying to update prices");
    monitoringService.updatePrices(liveController::onPriceChanged);
  }

}
