package com.liberty.service.strategy;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.adapters.MinerAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
@Primary
public class AutomaticSellStrategy implements SellStrategy {

    @Autowired
    private MinerAdapter minerAdapter;

    @Override
    public boolean shouldSell(ItemData itemData, PlayerTradeStatus playerTradeStatus) {
        if (!minerAdapter.isAlive()) return false;
        return minerAdapter.shouldSellPlayer(itemData.getId(), itemData.getLastSalePrice());
    }

    @Override
    public SellRequest defineBid(ItemData itemData, PlayerTradeStatus tradeStatus) {
        MinerBid minerBid = minerAdapter.defineBid(itemData.getId(), itemData.getLastSalePrice());
        SellRequest request = new SellRequest();
        request.setPlayerId(tradeStatus.getId());
        request.setItemId(itemData.getId());
        request.setTradeId(itemData.getTradeId());
        request.setStartPrice(minerBid.getSellStartPrice());
        request.setBuyNow(minerBid.getSellBuyNowPrice());
        return request;
    }

    @Override
    public boolean isPriceDistributionActual(Long id) {
        return minerAdapter.isPriceDistributionActual(id);
    }

    @Data
    @AllArgsConstructor
    public static class MinerBid {
        private int sellStartPrice;
        private int sellBuyNowPrice;
    }
}
