package com.liberty.service.impl;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.service.AutoTradingService;
import com.liberty.service.TagService;
import com.liberty.service.TradeService;
import com.liberty.service.adapters.MinerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Service
@Slf4j
public class AutoTradingServiceImpl implements AutoTradingService {

    public static final String MINER_TAG = "Miner";
    @Autowired
    private TagService tagService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private MinerAdapter miner;

    @Override
    public void updateActivePlayers() {
        if (!miner.isAlive()) {
            log.error("Can not update active players. Fifa Miner is broken");
            return;
        }
        List<PlayerTradeStatus> toActivate = miner.getActivePlayers();
        log.info("Trying to activate " + toActivate.size() + " players");

        tradeService.disableAll();
        for (PlayerTradeStatus tradeStatus : toActivate) {
            tradeService.enablePlayer(tradeStatus.getId(), tradeStatus.getMaxPrice(), MINER_TAG);
            log.info("Activated " + tradeStatus.getName() + " for buy now " + tradeStatus.getMaxPrice());
        }

    }
}
