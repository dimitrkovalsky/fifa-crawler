package com.liberty.schedule;

import com.liberty.service.TradeService;
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
public class CheckUnassignedTask {

  @Autowired
  private TradeService tradeService;

  @Autowired
  private LogController logController;

  @Scheduled(fixedRate = 300_000)
  public void check() {
    log.info("Trying to update tradepile");
    tradeService.getTradePileSize();
    logController.info("Checked tradepile");
  }

}