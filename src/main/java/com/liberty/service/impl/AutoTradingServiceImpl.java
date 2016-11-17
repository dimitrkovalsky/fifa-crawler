package com.liberty.service.impl;

import com.fifaminer.client.dto.SellBuyNowStrategy;
import com.fifaminer.client.dto.SettingConfigurationTO;
import com.fifaminer.client.dto.SettingTO;
import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.service.AutoTradingService;
import com.liberty.service.NoActivityService;
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

    @Autowired
    private NoActivityService noActivityService;

    private boolean priceValid = false;
    private boolean updateRequested = false;

    @Override
    public void updateActivePlayers() {
        if (!miner.isAlive()) {
            log.error("Can not update active players. Fifa Miner is broken");
            return;
        }
        if (priceValid) {
            List<PlayerTradeStatus> toActivate = miner.getPlayersToBuy();
            log.info("Trying to activate " + toActivate.size() + " players");

            tradeService.disableAll();
            for (PlayerTradeStatus tradeStatus : toActivate) {
                tradeService.enablePlayer(tradeStatus.getId(), tradeStatus.getMaxPrice(), MINER_TAG);
                log.info("Activated " + tradeStatus.getName() + " for buy now " + tradeStatus.getMaxPrice());
            }
        } else {
            if (!updateRequested)
                checkUpdates();
            log.info("Can not perform active players update. Prices is too old.");
        }

    }

    public void changeStrategy() {
        SettingConfigurationTO configuration = new SettingConfigurationTO(SettingTO.MAX_BUY_PRICE_STRATEGY,
                SellBuyNowStrategy.FORECASTED_MEDIAN);
        miner.changeStrategy(configuration);
    }

    @Override
    public void checkUpdates() {
        int wait = 0;
        while (!miner.isAlive()) {
            DelayHelper.waitStrict(1000);
            wait++;
            if (wait >= 300)
                return;
        }

        List<Long> ids = miner.getPlayersForUpdate();
        if (!noActivityService.isUpdateInProgress()) {
            noActivityService.shouldUpdate(ids, () -> {
                priceValid = true;
                updateRequested = false;
            });
            updateRequested = true;
            log.info("[AutoTradingService] Placed to price update " + ids.size() + " players");
        }
    }

}
