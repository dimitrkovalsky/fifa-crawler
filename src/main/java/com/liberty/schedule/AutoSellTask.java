package com.liberty.schedule;

import com.liberty.service.AutoSellService;
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
public class AutoSellTask {

    @Autowired
    private AutoSellService autoSellService;

    @Autowired
    private LogController logController;

    @Scheduled(fixedRate = 200_000, initialDelay = 200_000)
    public void check() {
        log.info("Trying to sell unassigned players");
        autoSellService.trySell();
    }

}