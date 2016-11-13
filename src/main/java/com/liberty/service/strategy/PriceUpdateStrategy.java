package com.liberty.service.strategy;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;

import java.util.Set;

/**
 * User: Dimitr
 * Date: 13.11.2016
 * Time: 19:01
 */
public interface PriceUpdateStrategy {

  Set<AuctionInfo> findPlayers(PlayerTradeStatus tradeStatus);

  int getLastBound();
}
