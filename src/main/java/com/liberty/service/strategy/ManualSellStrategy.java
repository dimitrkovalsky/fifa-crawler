package com.liberty.service.strategy;

import com.liberty.common.PriceHelper;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
@Slf4j
@Primary
public class ManualSellStrategy implements SellStrategy {

    private static final int LOW_PROFIT_THRESHOLD = 100;

    @Override
    public boolean shouldSell(ItemData itemData, PlayerTradeStatus playerTradeStatus) {
        Integer startPrice = playerTradeStatus.getSellStartPrice();
        Integer boughtFor = itemData.getLastSalePrice();
        int profit = PriceHelper.calculateProfit(boughtFor, startPrice);
        if (profit < LOW_PROFIT_THRESHOLD) {
            log.error("Can not sell " + playerTradeStatus.getName() +
                    ". Profit is to low. Bought for : " + boughtFor + " .Sell price : " + startPrice);
            return false;
        }
        return true;
    }

    @Override
    public SellRequest defineBid(ItemData itemData, PlayerTradeStatus tradeStatus) {
        Integer startPrice = tradeStatus.getSellStartPrice();
        Integer boughtFor = itemData.getLastSalePrice();
        int profitBid = PriceHelper.calculateProfit(boughtFor, startPrice);

        SellRequest request = new SellRequest();
        request.setPlayerId(tradeStatus.getId());
        request.setItemId(itemData.getId());
        request.setTradeId(itemData.getTradeId());
        request.setStartPrice(tradeStatus.getSellStartPrice());
        request.setBuyNow(tradeStatus.getSellBuyNowPrice());
        return request;
    }
}
