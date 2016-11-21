package com.liberty.service.impl;

import com.liberty.common.PriceHelper;
import com.liberty.listeners.ParameterUpdateListener;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.AutoSellService;
import com.liberty.service.NoActivityService;
import com.liberty.service.TradeService;
import com.liberty.service.UserParameterService;
import com.liberty.service.strategy.SellStrategy;
import com.liberty.websockets.LogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.liberty.service.impl.ASellService.TRADEPILE_SIZE;
import static java.util.function.UnaryOperator.identity;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Service
@Slf4j
public class AutoSellServiceImpl implements AutoSellService, InitializingBean, ParameterUpdateListener {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerTradeStatusRepository tradeStatusRepository;

    @Autowired
    private SellStrategy sellStrategy;

    @Autowired
    private UserParameterService parameterService;

    @Autowired
    private NoActivityService noActivityService;

    @Autowired
    private LogController logController;

    private boolean autoSellEnabled;

    @Override
    public void trySell() {
        if (!autoSellEnabled) {
            log.info("Auto Sell disabled");
            return;
        }
        List<ItemData> unassigned = tradeService.getAllUnassigned();
        log.info("Found " + unassigned.size() + " unassigned players");
        if (unassigned.isEmpty()) {
            log.info("There are no unassigned players...");
            return;
        }
        int canSell = TRADEPILE_SIZE - tradeService.getTradePileSize();
        if (canSell <= 0) {
            log.info("Can not sell any player. Tradepile is full.");
            return;
        }
        List<Long> ids = unassigned.stream().map(ItemData::getAssetId).collect(Collectors.toList());
        Map<Long, PlayerTradeStatus> players = getTradeMap(ids);
        Set<Long> toUpdate = new HashSet<>();
        for (ItemData itemData : unassigned) {
            PlayerTradeStatus tradeStatus = players.get(itemData.getAssetId());
            if (!sellStrategy.isPriceDistributionActual(itemData.getAssetId())) {
                continue;
            }

            if (shouldSell(itemData, tradeStatus)) {
                if (!autoSellEnabled) {
                    log.info("Auto sell disabled");
                    return;
                }
                SellRequest sellRequest = getSellRequest(itemData, tradeStatus);
                tradeService.sellAuto(sellRequest);
                canSell--;
            }
            if (canSell <= 0) {
                break;
            }
        }
        toUpdate.addAll(unassigned.stream()
                .filter(itemData -> !sellStrategy.isPriceDistributionActual(itemData.getAssetId()))
                .map(ItemData::getAssetId)
                .collect(Collectors.toList()));

        if (!toUpdate.isEmpty()) {
            noActivityService.shouldUpdate(toUpdate);
            log.info("AutoSellService asked to update prices for " + toUpdate.size() + " players");
        }
    }

    private SellRequest getSellRequest(ItemData itemData, PlayerTradeStatus tradeStatus) {
        SellRequest sellRequest = sellStrategy.defineBid(itemData, tradeStatus);
        int from = PriceHelper.calculateProfit(itemData.getLastSalePrice(), sellRequest.getStartPrice());
        int to = PriceHelper.calculateProfit(itemData.getLastSalePrice(), sellRequest.getBuyNow());
        logController.info("Trying to place to market " + tradeStatus.getName() + " . Profit from " + from + " to " + to);
        return sellRequest;
    }

    private boolean shouldSell(ItemData itemData, PlayerTradeStatus tradeStatus) {
//        return tradeStatus.isAutoSellEnabled() && sellStrategy.shouldSell(itemData, tradeStatus);
        return sellStrategy.shouldSell(itemData, tradeStatus);
    }


    private Map<Long, PlayerTradeStatus> getTradeMap(List<Long> ids) {
        return StreamSupport.stream(tradeStatusRepository.findAll(ids).spliterator(), false)
                .collect(Collectors.toMap(PlayerTradeStatus::getId, identity()));
    }

    @Override
    public void onParameterUpdate(ParameterUpdateRequest request) {
        if (request.getAutoSellEnabled() != null)
            autoSellEnabled = request.getAutoSellEnabled();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.autoSellEnabled = parameterService.getUserParameters().isAutoSellEnabled();
        parameterService.subscribe(this);
    }
}
