package com.liberty.service;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.SellRequest;
import com.liberty.websockets.BuyMessage;

import java.util.List;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:12
 */
public interface TradeService {

  void removeAllPlayers();

  int getTradePileSize();

  List<GroupedToSell> getUnassigned();

  void updatePrices();

  void checkMarket();

  void addToAutoBuy(String name, long id, int maxPrice);

  void findMinPriceAll();

  PlayerStatistic findMinPrice(long playerId);

  MarketInfo getMarketInfo();

  void setMarketInfo(MarketInfo info);

  void updateTokens(String sessionId, String phishingToken, Boolean external);

  void autoBuy(AutobuyRequest run);

  void deleteFromAutoBuy(Long id);

  PlayerStatistic getMinPrice(Long id);

  List<PlayerTradeStatus> getAllToAutoBuy();

  PlayerTradeStatus getOnePlayer(Long id);

  void updateAutoBuy(BuyRequest request);

  void updatePlayer(PlayerTradeStatus request);

  void sell(SellRequest request);

  List<PlayerTradeStatus> search(String phrase);

  BuyMessage getTradepileInfo();

  void logBuyOrSell();
}
