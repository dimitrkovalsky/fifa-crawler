package com.liberty.service.impl;

import com.liberty.common.PriceHelper;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.AutoSellService;
import com.liberty.service.TradeService;
import com.liberty.service.strategy.SellStrategy;
import com.liberty.websockets.LogController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
public class AutoSellServiceImpl implements AutoSellService {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerTradeStatusRepository tradeStatusRepository;

    @Autowired
    private SellStrategy sellStrategy;

    @Autowired
    private LogController logController;

    @Override
    public void trySell() {
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
        for (ItemData itemData : unassigned) {
            PlayerTradeStatus tradeStatus = players.get(itemData.getAssetId());
            if (shouldSell(itemData, tradeStatus)) {
                SellRequest sellRequest = getSellRequest(itemData, tradeStatus);
                tradeService.sell(sellRequest);
            }
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
        return tradeStatus.isAutoSellEnabled() && sellStrategy.shouldSell(itemData, tradeStatus);
    }


    private Map<Long, PlayerTradeStatus> getTradeMap(List<Long> ids) {
        return StreamSupport.stream(tradeStatusRepository.findAll(ids).spliterator(), false)
                .collect(Collectors.toMap(PlayerTradeStatus::getId, identity()));
    }

}
