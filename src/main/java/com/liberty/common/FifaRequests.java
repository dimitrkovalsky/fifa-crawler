package com.liberty.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.AuthRequest;
import com.liberty.model.AuthResponse;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.Bid;
import com.liberty.model.market.BuyResponse;
import com.liberty.model.market.FifaError;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.Items;
import com.liberty.model.market.SellItem;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.FifaEndpoints.AUTH;
import static com.liberty.common.UrlResolver.*;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests extends BaseFifaRequests {

  private String sessionId = null;
  private String phishingToken = null;

  public List<AuctionInfo> getTradePile() {
    HttpPost request = createRequest(getTradeLineUrl());
    String json = execute(request).get();
    if (isError(json)) {
      return Collections.emptyList();
    }
    Optional<TradeStatus> trade = JsonHelper.toEntity(json, TradeStatus.class);

    return trade.get().getAuctionInfo();
  }

  public Watchlist getWatchlist() {
    HttpPost request = createRequest(getWatchlistUrl());
    String json = execute(request).get();
    if (isError(json)) {
      return null;
    }
    Optional<Watchlist> watchlist =  JsonHelper.toEntity(json, Watchlist.class);

    return watchlist.get();
  }

  public Optional<TradeStatus> searchPlayer(long id, int maxPrice, int page) throws IOException {
    HttpPost request = createRequest(String.format(getSearchUrl(), page, id, maxPrice));
    Optional<String> execute = execute(request);
    if (!execute.isPresent()) {
      log.error("Player not found");
      return Optional.empty();
    }
    String json = execute.get();
    if (isError(json)) {
      return Optional.empty();
    }
    return JsonHelper.toEntity(json, TradeStatus.class);
  }

  private boolean isError(String json) {
    ObjectMapper objectMapper = JsonHelper.getObjectMapper();
    try {
      FifaError fifaError = objectMapper.readValue(json, FifaError.class);
      log.error("FIFA ERROR : " + fifaError.getReason());
      if (fifaError.getCode() == FifaError.ErrorCode.SESSION_EXPIRED) {
        return updateSession();
      }
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  /**
   * Returns true if error
   */
  private boolean updateSession() {
    Optional<String> auth = auth();
    if (!auth.isPresent()) {
      return true;
    }
    sessionId = auth.get();
    return false;
  }

  private String getAuthRequest() {
    AuthRequest request = new AuthRequest(2311254984L, 228045231L);
    return JsonHelper.toJson(request).toString();
  }

  public String getSessionId() {
    if (sessionId == null) {
      log.error("sessionId is null. Waiting until session will be updated");
      try {
        synchronized (this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
    return sessionId;
  }

  /**
   * Returns current session id not blocked.
   */
  public String getSessionForCheck() {
    return sessionId;
  }

  public String getPhishingTokenForCheck() {
    return sessionId;
  }

  public String getPhishingToken() {
    if (phishingToken == null) {
      log.error("phishingToken is null. Waiting to phishingToken will be updated");
      try {
        synchronized (this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
    return phishingToken;
  }

  public void setPhishingToken(String phishingToken) {
    this.phishingToken = phishingToken;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
    log.info("Updated session id to " + sessionId);
    synchronized (this) {
      this.notifyAll();
    }
  }

  public Optional<String> auth() {
    try {
      HttpPost request = createAuthRequest(AUTH);
      String authRequest = getAuthRequest();
      StringEntity entity = new StringEntity(authRequest);
      entity.setContentType("application/json;charset=utf-8");
      log.info("Auth request " + authRequest);
      request.setEntity(entity);
      Optional<String> result = execute(request);
      if (!result.isPresent()) {
        return Optional.empty();
      }
      Optional<AuthResponse> response = JsonHelper.toEntity(result.get(), AuthResponse.class);
      if (!response.isPresent()) {
        return Optional.empty();
      }
      log.info("Retrieved session >>> " + response.get());
      String sid = response.get().getSid();
      return Optional.ofNullable(sid);
    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  private void status(AuctionInfo auctionInfo) {
    try {
      HttpPost request = createRequest(String.format(getStatusUrl(), auctionInfo.getTradeId()));
      log.info(request.toString());
      Optional<String> execute = execute(request);
      Optional<BuyResponse> buy = JsonHelper.toEntity(execute.get(), BuyResponse.class);
    } catch (Exception e) {
      log.error("Buy status error : " + e.getMessage());
    }
  }

  public boolean buy(AuctionInfo auctionInfo) {
    try {
      status(auctionInfo);
      Long tradeId = auctionInfo.getTradeId();
      log.info("Trying to bid : " + tradeId + " for " + auctionInfo.getBuyNowPrice());
      HttpPost request = createBidRequest(String.format(getBidUrl(), tradeId));
      Bid bid = new Bid(auctionInfo.getBuyNowPrice());
      String json = JsonHelper.toJsonString(bid);
      request.setEntity(new StringEntity(json));
      Optional<String> response = execute(request);
      if (!response.isPresent()) {
        return false;
      }
      Optional<BuyResponse> buy = JsonHelper.toEntity(response.get(), BuyResponse.class);
      if (!buy.isPresent()) {
        return false;
      }
      log.info("Buy response : " + buy.get());
      if (buy.get().getErrorState() != null) {
        log.error("Buy error : " + buy.get().getErrorState());
        return false;
      }
      return true;
    } catch (Exception e) {
      log.error("Buy error : " + e.getMessage());
      return false;
    }
  }

  public boolean item(Long itemId) {
    HttpPost request = createPutRequest(getItemUrl());
    SellItem toSell = new SellItem(itemId);

    try {
      request.setEntity(new StringEntity(JsonHelper.toJsonString(toSell)));
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }
    String json = execute(request).get();
    if (isError(json)) {
      return false;
    }
    Optional<SellItem> trade = JsonHelper.toEntity(json, SellItem.class);
    return trade.get().getItemData().get(0).getSuccess();
  }

  public boolean auctionHouse(Long id, int startPrice, int buyNow) {
    HttpPost request = createPostRequest(getAuctionHouseUrl());
    AuctionInfo toSell = new AuctionInfo();
    toSell.setStartingBid(startPrice);
    toSell.setBuyNowPrice(buyNow);
    toSell.setDuration(3600L);
    ItemData itemData = new ItemData();
    itemData.setId(id);
    toSell.setItemData(itemData);
    try {
      request.setEntity(new StringEntity(JsonHelper.toJsonString(toSell)));
      execute(request);
      return true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return false;
  }

  public void removeAllSold() {
    HttpPost deleteRequest = createDeleteRequest(getRemoveSold());
    execute(deleteRequest);
  }


  public void relistAll() {
    HttpPost putRequest = createPutRequest(getRelistUrl());
    execute(putRequest);
  }

  public List<ItemData> getUnassigned() {
    HttpPost request = createRequest(getGetUnassignedUrl());
    String json = execute(request).get();
    if (isError(json)) {
      return Collections.emptyList();
    }
    Optional<Items> trade = JsonHelper.toEntity(json, Items.class);
    return trade.get().getItemData();
  }



}
