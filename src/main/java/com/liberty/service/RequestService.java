package com.liberty.service;

import com.liberty.common.AuctionHouseResponse;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.request.TokenUpdateRequest;
import com.liberty.rest.response.BidStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author Dmytro_Kovalskyi.
 * @since 26.10.2016.
 */
public interface RequestService {

  boolean buy(AuctionInfo auctionInfo);

  List<AuctionInfo> getTradePile();

  void removeAllSold();

  void relistAll();

  List<ItemData> getUnassigned();

  boolean item(Long itemId);

  List<ItemData> getMyPlayers();

  boolean item(Long itemId, Long tradeId);

  Optional<AuctionHouseResponse> auctionHouse(Long itemId, Integer startPrice, Integer buyNow);

  Watchlist getWatchlist();

  Optional<TradeStatus> searchPlayer(Long id, Integer maxPrice, int page);

  String getPhishingTokenForCheck();

  String getSessionForCheck();

  void removeFromTargets(Long tradeId);

  TradeStatus getTradeStatus(Long tradeId);

  BidStatus makeBid(Long tradeId, Long price);

  Optional<TradeStatus> search(MarketSearchRequest searchRequest);

  void updateCredentials(String sessionId, String phishingToken);

  void updateAuthTokens(String sessionId, List<TokenUpdateRequest.Cookie> cookies);

  void updateTokens(String sessionId, String phishingToken, List<TokenUpdateRequest.Cookie> cookies);

  int getRequestRate();
}
