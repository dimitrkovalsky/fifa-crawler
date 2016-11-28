package com.liberty.rest;

import com.liberty.rest.response.MinerSellResponse;
import com.liberty.service.adapters.MinerAdapter;
import com.liberty.service.strategy.AutomaticSellStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: Dimitr
 * Date: 27.11.2016
 * Time: 10:58
 */
@RestController
@RequestMapping("/api/miner")
@Slf4j
public class MinerResource {

    @Autowired
    private MinerAdapter adapter;

    @RequestMapping(path = "/sell/price/{playerId}", method = RequestMethod.GET)
    public MinerSellResponse getInfo(@PathVariable Long playerId) {
        if (adapter.isAlive()) {
            return new MinerSellResponse(adapter.defineSellBid(playerId), adapter.isPriceDistributionActual(playerId));
        }
        return new MinerSellResponse(new AutomaticSellStrategy.MinerBid(0, 0), false);
    }
}
