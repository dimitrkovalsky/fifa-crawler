package com.liberty.schedule;

import com.liberty.service.AutoTradingService;
import com.liberty.websockets.LogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@Component
@Slf4j
public class AutoTradingTask {

    @Autowired
    private AutoTradingService autoTradingService;

    @Autowired
    private LogController logController;

    @Scheduled(fixedRate = 200_000, initialDelay = 100_000)
    public void check() {
        log.info("Trying to update players for buy");
        autoTradingService.updateActivePlayers();
    }

}