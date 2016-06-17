package com.liberty.service;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.rest.request.BuyRequest;

import java.util.List;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:12
 */
public interface TradeService {

  void removeAllPlayers();

  void checkMarket();

  void addToAutoBuy(String name, long id, int maxPrice);

  PlayerStatistic findMinPrice(long playerId);

  MarketInfo getMarketInfo();

  void setMarketInfo(MarketInfo info);

  void autoBuy(boolean run);

  void deleteFromAutoBuy(Long id);

  PlayerStatistic getMinPrice(Long id);

  List<PlayerTradeStatus> getAllToAutoBuy();

  PlayerTradeStatus getOnePlayer(Long id);

  void updateAutoBuy(BuyRequest request);

  void updatePlayer(PlayerTradeStatus request);
}
