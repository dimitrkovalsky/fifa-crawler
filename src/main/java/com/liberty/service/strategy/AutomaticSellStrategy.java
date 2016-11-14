package com.liberty.service.strategy;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
public class AutomaticSellStrategy implements SellStrategy {
    @Override
    public boolean shouldSell(ItemData itemData, PlayerTradeStatus playerTradeStatus) {
        return false;
    }

    @Override
    public SellRequest defineBid(ItemData itemData, PlayerTradeStatus tradeStatus) {
        return null;
    }
}
