package com.liberty.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.AuthRequest;
import com.liberty.model.AuthResponse;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.Bid;
import com.liberty.model.market.BuyResponse;
import com.liberty.model.market.FifaError;
import com.liberty.model.market.TradeStatus;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.FifaEndpoints.AUTH;
import static com.liberty.common.FifaEndpoints.BID_URL;
import static com.liberty.common.FifaEndpoints.ITEM_URL;
import static com.liberty.common.FifaEndpoints.SEARCH_URL;
import static com.liberty.common.FifaEndpoints.STATUS_URL;
import static com.liberty.common.FifaEndpoints.TRADE_LINE_URL;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests extends BaseFifaRequests {

  private String sessionId = "52236eb0-0546-4486-9efe-3899669af0de";
  private String phishingToken = "4111576680866094587";

  public FifaRequests() {
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.conn", "DEBUG");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.impl.client", "DEBUG");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.client", "DEBUG");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
  }

  public void getTradeLine() {
    HttpPost request = createRequest(TRADE_LINE_URL);
    Optional<String> result = execute(request);
    System.out.println(result);
  }

  public Optional<TradeStatus> searchPlayer(long id, int maxPrice) throws IOException {
    HttpPost request = createRequest(String.format(SEARCH_URL, id, maxPrice));
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
    if (!auth.isPresent())
      return true;
    sessionId = auth.get();
    return false;
  }

  private String getAuthRequest() {
    AuthRequest request = new AuthRequest(2311254984L, 228045231L);
    return JsonHelper.toJson(request).toString();
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getPhishingToken() {
    return phishingToken;
  }

  public void setPhishingToken(String phishingToken) {
    this.phishingToken = phishingToken;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
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
      if (!result.isPresent())
        return Optional.empty();
      Optional<AuthResponse> response = JsonHelper.toEntity(result.get(), AuthResponse.class);
      if (!response.isPresent())
        return Optional.empty();
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
      HttpPost request = createRequest(String.format(STATUS_URL, auctionInfo.getTradeId()));
      log.info(request.toString());
      Optional<String> execute = execute(request);
      Optional<BuyResponse> buy = JsonHelper.toEntity(execute.get(), BuyResponse.class);
//      log.info("Status for : " + auctionInfo.getTradeId() + " " + buy.get().getAuctionInfo().get(0)
//          .getBidState());
    } catch (Exception e) {
      log.error("Buy status error : " + e.getMessage());
    }
  }


  public boolean buy(AuctionInfo auctionInfo) {
    try {
      status(auctionInfo);
      Long tradeId = auctionInfo.getTradeId();
      log.info("Trying to bid : " + tradeId + " for " + auctionInfo.getBuyNowPrice());
      HttpPost request = createBidRequest(String.format(BID_URL, tradeId));
      Bid bid = new Bid(auctionInfo.getBuyNowPrice());
      String json = JsonHelper.toJsonString(bid);
      request.setEntity(new StringEntity(json));
      Optional<String> response = execute(request);
      if (!response.isPresent())
        return false;
      Optional<BuyResponse> buy = JsonHelper.toEntity(response.get(), BuyResponse.class);
      if (!buy.isPresent())
        return false;
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

  public void item() {
    HttpPost request = createRequest(ITEM_URL);
//    SellItem item = new SellItem();
//    request.setEntity(new StringEntity());
  }

  public void auctionHouse() {

  }
}
