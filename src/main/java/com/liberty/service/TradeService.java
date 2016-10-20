package com.liberty.service;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.TradeInfo;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.AutobidRequest;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.request.SellRequest;
import com.liberty.rest.response.BidStatus;
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

  List<PlayerInfo> getAllToAutoBuy();

  PlayerTradeStatus getOnePlayer(Long id);

  PlayerInfo getPlayerInfo(Long id);

  void updateAutoBuy(BuyRequest request);

  void updatePlayer(PlayerTradeStatus request);

  void sell(SellRequest request);

  List<PlayerTradeStatus> search(String phrase);

  BuyMessage getTradepileInfo();

  Watchlist getWatchlist();

  void logBuyOrSell();

  List<AuctionInfo> getTransferTargets();

  void removeExpired(List<AuctionInfo> expired);

  void removeFromTargets(Long tradeId);

  TradeStatus getTradeStatus(Long tradeId);

  BidStatus makeBid(Long tradeId, Long price);

  List<TradeInfo> search(MarketSearchRequest searchRequest);

  void addToAutoBid(AutobidRequest bidRequest);
}
