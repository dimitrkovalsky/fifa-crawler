package com.liberty.schedule;

import com.liberty.service.TradeService;
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
public class MarketTask {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private LogController logController;

    @Scheduled(fixedRate = 200_000)
    public void monitor() {
        logController.info("Trying to check market");
        tradeService.checkMarket();
    }


    private void add(String name, long id, int maxPrice) {
        tradeService.addToAutoBuy(name, id, maxPrice);
    }
}