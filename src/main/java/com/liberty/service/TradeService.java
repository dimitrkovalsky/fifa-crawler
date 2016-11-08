package com.liberty.service;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.AutobidRequest;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.SellRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.websockets.BuyMessage;

import java.util.List;
import java.util.Set;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:12
 */
public interface TradeService {

  void removeAllPlayers();

  int getTradePileSize();

  List<GroupedToSell> getUnassigned();

  boolean isActive();

  void checkMarket();

  boolean buyPlayer(long playerId, int maxPrice, String playerName);

  void addToAutoBuy(String name, long id, int maxPrice);

  MarketInfo getMarketInfo();

  void autoBuy(AutobuyRequest run);

  void deleteFromAutoBuy(Long id);

  List<PlayerInfo> getAllToAutoBuy();

  List<PlayerInfo> getAllToAutoBuy(Set<String> tags);

  PlayerInfo getPlayerInfo(Long id);

  void updateAutoBuy(BuyRequest request);

  void updatePlayer(PlayerTradeStatus request);

  void sell(SellRequest request);

  BuyMessage getTradepileInfo();

  Watchlist getWatchlist();

  void logBuyOrSell();

  List<AuctionInfo> getTransferTargets();

  void removeExpired(List<AuctionInfo> expired);

  void removeFromTargets(Long tradeId);

  TradeStatus getTradeStatus(Long tradeId);

  BidStatus makeBid(Long tradeId, Long price);

  void addToAutoBid(AutobidRequest bidRequest);

  Set<String> getActiveTags();
}
