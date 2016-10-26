package com.liberty.service.impl;

import com.liberty.common.AuctionHouseResponse;
import com.liberty.common.DelayHelper;
import com.liberty.common.FifaCrawlerState;
import com.liberty.common.FifaRequests;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.request.TokenUpdateRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.RequestService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.liberty.common.FifaCrawlerState.FAILED;
import static com.liberty.common.FifaCrawlerState.READY;
import static com.liberty.common.FifaCrawlerState.TOKEN_NULL;
import static com.liberty.common.FifaCrawlerState.WORKING;

/**
 * @author Dmytro_Kovalskyi.
 * @since 26.10.2016.
 */
@Service
public class RequestServiceImpl implements RequestService {

  private volatile FifaCrawlerState currentState = TOKEN_NULL;

  @Autowired
  private LogController logController;

  private FifaRequests fifaRequests = new FifaRequests(this::onError, this::onStatusChange);

  private void onError(String msg) {
    logController.error(msg);
  }

  private void onStatusChange(FifaCrawlerState state) {
    currentState = state;
  }

  private <T> T execute(Supplier<T> function) {
    waitReady();
    currentState = WORKING;
    T result = function.get();
    currentState = READY;
    return result;
  }

  private void execute(Runnable function) {
    waitReady();
    currentState = WORKING;
    function.run();
    currentState = READY;
  }

  private void waitReady() {
    int times = 0;
    while (currentState != READY) {
      DelayHelper.waitStrict(10);
      times++;
      if (currentState == FAILED && times % 1000 == 0) {
        System.err.println("FAILED thread : " + Thread.currentThread().getName());
      }
    }
  }

  @Override
  public List<AuctionInfo> getTradePile() {
    return execute(() -> fifaRequests.getTradePile());
  }

  @Override
  public Watchlist getWatchlist() {
    return execute(() -> fifaRequests.getWatchlist());
  }

  @Override
  public Optional<TradeStatus> searchPlayer(Long id, Integer maxPrice, int page) {
    return execute(() -> fifaRequests.searchPlayer(id, maxPrice, page));
  }

  @Override
  public String getSessionForCheck() {
    return fifaRequests.getSessionForCheck();
  }

  @Override
  public String getPhishingTokenForCheck() {
    return fifaRequests.getPhishingTokenForCheck();
  }

  @Override
  public boolean buy(AuctionInfo auctionInfo) {
    return execute(() -> fifaRequests.buy(auctionInfo));
  }

  @Override
  public boolean item(Long itemId, Long tradeId) {
    return execute(() -> fifaRequests.item(itemId, tradeId));
  }

  @Override
  public Optional<AuctionHouseResponse> auctionHouse(Long itemId, Integer startPrice,
                                                     Integer buyNow) {
    return execute(() -> fifaRequests.auctionHouse(itemId, startPrice, buyNow));
  }

  @Override
  public boolean item(Long itemId) {
    return execute(() -> fifaRequests.item(itemId));
  }

  @Override
  public void removeAllSold() {
    execute(() -> fifaRequests.removeAllSold());
  }

  @Override
  public void relistAll() {
    execute(() -> fifaRequests.relistAll());
  }

  @Override
  public List<ItemData> getUnassigned() {
    return execute(() -> fifaRequests.getUnassigned());
  }

  @Override
  public void removeFromTargets(Long tradeId) {
    execute(() -> fifaRequests.removeFromTargets(tradeId));

  }

  @Override
  public TradeStatus getTradeStatus(Long tradeId) {
    return execute(() -> fifaRequests.getTradeStatus(tradeId));
  }

  @Override
  public BidStatus makeBid(Long tradeId, Long price) {
    return execute(() -> fifaRequests.makeBid(tradeId, price));
  }

  @Override
  public Optional<TradeStatus> search(MarketSearchRequest searchRequest) {
    return execute(() -> fifaRequests.search(searchRequest));
  }

  @Override
  public void updateCredentials(String sessionId, String phishingToken) {
    updateTokens(sessionId, phishingToken, null);
  }

  @Override
  public void updateAuthTokens(String sessionId, List<TokenUpdateRequest.Cookie> cookies) {
    if (!sessionId.equals(getSessionForCheck())) {
      fifaRequests.setSessionId(sessionId);
      logController.info("Updated sessionId to " + sessionId);
    }
    fifaRequests.updateAuthCookies(cookies);
    currentState = READY;
  }

  @Override
  public void updateTokens(String sessionId, String phishingToken,
                           List<TokenUpdateRequest.Cookie> cookies) {
    if (!phishingToken.equals(getPhishingTokenForCheck())) {
      fifaRequests.setPhishingToken(phishingToken);
      logController.info("Updated phishingToken to " + phishingToken);
    }
    if (!sessionId.equals(getSessionForCheck())) {
      fifaRequests.setSessionId(sessionId);
      logController.info("Updated sessionId to " + sessionId);
    }
    if (cookies != null) {
      fifaRequests.updateCookies(cookies);
    }
    currentState = READY;
  }
}
