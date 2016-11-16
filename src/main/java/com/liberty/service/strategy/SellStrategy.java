package com.liberty.service.strategy;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
public interface SellStrategy {
    boolean shouldSell(ItemData itemData, PlayerTradeStatus playerTradeStatus);

    SellRequest defineBid(ItemData itemData, PlayerTradeStatus tradeStatus);

    boolean isPriceDistributionActual(Long id);
}
