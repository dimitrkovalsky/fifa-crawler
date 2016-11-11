package com.liberty.schedule;

import com.liberty.robot.AuctionRobot;
import com.liberty.service.NoActivityService;
import com.liberty.service.RequestService;
import com.liberty.service.TradeService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.service.impl.NoActivityServiceImpl.REQUEST_PER_MINUTE;

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

  @Autowired
  private AuctionRobot robot;

  @Autowired
  private TradeService tradeService;

  private boolean enabled = true;

  @Autowired
  private LogController logController;

  @Scheduled(fixedRate = 120_000, initialDelay = 120_000)
  public void monitor() {
    if (!enabled) {
      return;
    }
    if (tradeService.isActive() || !robot.isDisabled()) {
      log.info("Can not run no activity service. Trade service or Auction Robot is active.");
      return;
    }

    if (requestService.getRequestRate() < REQUEST_PER_MINUTE) {
      log.info("Trying to update player prices...");
      noActivityService.updatePlayerPrices();
    } else {
      log.info("Can not update player prices. Request rate too high");
    }

  }


}