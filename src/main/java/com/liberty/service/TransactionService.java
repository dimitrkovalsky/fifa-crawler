package com.liberty.service;

import com.liberty.model.market.AuctionInfo;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
public interface TransactionService {

    void logBuy(Long playerId, Long itemId, Long tradeId, Integer amount);

    void logSell(Long playerId, Long itemId, Long tradeId, Integer amount);

    void logPlaceToMarket(Long playerId, Long itemId, Long tradeId, Integer startBid, Integer buyNow);

    void logBuyByRobot(AuctionInfo auctionInfo);

    void logRelistOperation(Long playerId, Long itemId, Long tradeId, Integer startBid,
                            Integer buyNow);
}
