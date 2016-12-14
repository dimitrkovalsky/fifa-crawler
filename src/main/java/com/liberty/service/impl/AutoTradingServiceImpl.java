package com.liberty.service.impl;

import com.liberty.common.DelayHelper;
import com.liberty.listeners.ParameterUpdateListener;
import com.liberty.model.MinerStrategy;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.UserParameters;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.service.*;
import com.liberty.service.adapters.MinerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Service
@Slf4j
public class AutoTradingServiceImpl implements AutoTradingService, InitializingBean, ParameterUpdateListener {

    public static final String MINER_TAG = "Miner";
    @Autowired
    private TagService tagService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private MinerAdapter miner;

    @Autowired
    private NoActivityService noActivityService;

    @Autowired
    private PlayerTradeStatusRepository tradeStatusRepository;

    @Autowired
    private UserParameterService parameterService;

    private boolean priceValid = false;
    private boolean updateRequested = false;
    private boolean autoTradeOnlyActivePlayer;
    private boolean autoTradeEnabled;

    @Override
    public void updateActivePlayers() {
        if (!autoTradeEnabled)
            return;

        if (!autoTradeOnlyActivePlayer)
            autoPlayerUpdate();
        else
            updateOnlyActivePlayers();
    }

    private void updateOnlyActivePlayers() {
        if (!minerAlive()) return;

        List<PlayerTradeStatus> tradeStatuses = tradeStatusRepository.findAllByEnabled(true);
        Set<Long> toUpdate = new HashSet<>();
        AtomicInteger updated = new AtomicInteger(0);
        tradeStatuses.forEach(s -> {
            if (miner.isPriceDistributionActual(s.getId()) && !isSBC(s)) {
                PlayerTradeStatus pricesToBuy = miner.getPricesToBuy(s.getId());
                if (!pricesToBuy.getMaxPrice().equals(s.getMaxPrice())) {
                    tradeService.enablePlayer(pricesToBuy.getId(), pricesToBuy.getMaxPrice(), MINER_TAG);
                    updated.incrementAndGet();
                }
            } else {
                toUpdate.add(s.getId());
            }
        });
        if (!toUpdate.isEmpty()) {
            noActivityService.shouldUpdate(toUpdate);
            log.info("AutoTradingService asked to update prices for : " + toUpdate.size() + " players");
        }
        log.info("[AutoTradingService] updated prices for " + updated.get() + " players");
    }

    private boolean isSBC(PlayerTradeStatus status) {
        return status.getTags().contains("SBC");
    }

    private void autoPlayerUpdate() {
        if (!minerAlive()) return;
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

    private boolean minerAlive() {
        if (!miner.isAlive()) {
            log.error("Can not update active players. Fifa Miner is broken");
            return false;
        }
        return true;
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

    @Override
    public List<MinerStrategy> getBuyStrategies() {
        return miner.getBuyStrategies();
    }

    @Override
    public void activateBuyStrategy(Integer id) {
        log.info("Trying to activate buy strategy with id : " + id);
        miner.activateBuyStrategy(id);
    }

    @Override
    public void activateSellStrategy(Integer id) {
        log.info("Trying to activate sell strategy with id : " + id);
        miner.activateSellStrategy(id);
    }

    @Override
    public List<MinerStrategy> getSellStrategies() {
        return miner.getSellStrategies();
    }

    @Override
    public void onParameterUpdate(ParameterUpdateRequest request) {
        if (request.getAutoTradeEnabled() != null) {
            autoTradeEnabled = request.getAutoTradeEnabled();
        }
        if (request.getAutoTradeOnlyActivePlayer() != null) {
            autoTradeOnlyActivePlayer = request.getAutoTradeOnlyActivePlayer();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        parameterService.subscribe(this);
        UserParameters parameters = parameterService.getUserParameters();
        autoTradeEnabled = parameters.isAutoTradeEnabled();
        autoTradeOnlyActivePlayer = parameters.isAutoTradeOnlyActivePlayer();
    }
}
