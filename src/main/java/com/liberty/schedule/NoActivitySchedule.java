package com.liberty.schedule;

import com.liberty.service.NoActivityService;
import com.liberty.service.RequestService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@Component
@Slf4j
public class NoActivitySchedule {

  @Autowired
  private RequestService requestService;

  @Autowired
  private NoActivityService noActivityService;

  private boolean enabled = true;

  @Autowired
  private LogController logController;

  @Scheduled(fixedRate = 120_000, initialDelay = 120_000)
  public void monitor() {
    if (!enabled) {
      return;
    }

    if (requestService.getRequestRate() < 30) {
      log.info("Trying to update player prices...");
      noActivityService.updatePlayerPrices();
    } else {
      log.info("Can not update player prices. Request rate too high");
    }

  }


}