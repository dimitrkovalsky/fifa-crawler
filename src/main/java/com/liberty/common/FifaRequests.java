package com.liberty.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.AuthRequest;
import com.liberty.model.AuthResponse;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.Bid;
import com.liberty.model.market.BidResponse;
import com.liberty.model.market.FifaError;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.Items;
import com.liberty.model.market.SellItem;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.BidStatus;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.ErrorCode.NOT_ALLOWED;
import static com.liberty.common.FifaCrawlerState.FAILED;
import static com.liberty.common.FifaEndpoints.AUTH;
import static com.liberty.common.UrlResolver.getAuctionHouseUrl;
import static com.liberty.common.UrlResolver.getBidUrl;
import static com.liberty.common.UrlResolver.getGetUnassignedUrl;
import static com.liberty.common.UrlResolver.getItemUrl;
import static com.liberty.common.UrlResolver.getRelistUrl;
import static com.liberty.common.UrlResolver.getRemoveSold;
import static com.liberty.common.UrlResolver.getSearchUrl;
import static com.liberty.common.UrlResolver.getStatusUrl;
import static com.liberty.common.UrlResolver.getTradeLineUrl;
import static com.liberty.common.UrlResolver.getWatchlistUrl;
import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.id;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests extends BaseFifaRequests {

  public static final long NUCLEUS_PERSONA_ID = 228045231L;
  private volatile String sessionId = null;
  private volatile String phishingToken = null;

  private enum FifaRequestStatus {
    OK, SESSION_EXPIRED, FAILED
  }

  private Consumer<String> onError;

  private Consumer<FifaCrawlerState> onStateChange;

  public FifaRequests(Consumer<String> onError, Consumer<FifaCrawlerState> onStateChange) {
    this.onError = onError;
    this.onStateChange = onStateChange;
  }

  public List<AuctionInfo> getTradePile() {
    HttpPost request = createRequest(getTradeLineUrl());
    Optional<String> executionResult = execute(request);

    if (!executionResult.isPresent()) {
      return Collections.emptyList();
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    if (status == FifaRequestStatus.FAILED) {
      return Collections.emptyList();
    } else if (status == FifaRequestStatus.OK) {
      Optional<TradeStatus> entity = JsonHelper.toEntity(json, TradeStatus.class);
      return entity.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
    } else if (status == FifaRequestStatus.SESSION_EXPIRED) {
      return getTradePile();
    } else {
      return Collections.emptyList();
    }
  }

  public Optional<Watchlist> getWatchlist() {
    HttpPost request = createRequest(getWatchlistUrl());
    Optional<String> execute = execute(request);

    return processResult(execute, Watchlist.class, this::getWatchlist);
  }


  private <T, U> Optional<T> processResult(Optional<String> executionResult,
                                           Class<U> resultEntity,
                                           Supplier<Optional<T>> onSessionExpired) {
    if (!executionResult.isPresent()) {
      return Optional.empty();
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    if (status == FifaRequestStatus.FAILED) {
      return Optional.empty();
    } else if (status == FifaRequestStatus.OK) {
      return JsonHelper.toEntity(json, resultEntity);
    } else if (status == FifaRequestStatus.SESSION_EXPIRED) {
      return onSessionExpired.get();
    } else {
      return Optional.empty();
    }
  }

  public Optional<TradeStatus> searchPlayer(long id, int maxPrice, int page) {
    HttpPost request = createRequest(String.format(getSearchUrl(), page, id, maxPrice));
    Optional<String> execute = execute(request);

    return processResult(execute, TradeStatus.class, () -> searchPlayer(id, maxPrice, page));
  }


  private FifaRequestStatus getStatus(String json) {
    ObjectMapper objectMapper = JsonHelper.getObjectMapper();
    try {
      FifaError fifaError = objectMapper.readValue(json, FifaError.class);
      if (fifaError.getCode() == FifaError.ErrorCode.SESSION_EXPIRED) {
        logError("Session expired...");
        updateSession();
        return FifaRequestStatus.SESSION_EXPIRED;
      } else {
        log.error("FIFA ERROR : " + fifaError.getReason());
        return FifaRequestStatus.FAILED;
      }
    } catch (Exception ignored) {
      return FifaRequestStatus.OK;
    }
  }

  private void logError(String msg) {
    onError.accept(msg);
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
    AuthRequest request = new AuthRequest(2311254984L, NUCLEUS_PERSONA_ID);
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

  private Optional<String> auth() {
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
      Optional<AuthResponse> response =
          JsonHelper.toEntitySilently(result.get(), AuthResponse.class);
      if (!response.isPresent()) {
        setFailAuth();
        return Optional.empty();
      }
      log.info("Retrieved session >>> " + response.get());
      String sid = response.get().getSid();
      return Optional.ofNullable(sid);
    } catch (Exception e) {
      log.error("On auth can not parse response ");
      return Optional.empty();
    }
  }

  private void setFailAuth() {
    onStateChange.accept(FAILED);
  }

  private void status(Long tradeId) {
    try {
      HttpPost request = createRequest(String.format(getStatusUrl(), tradeId));
      log.info(request.toString());
      Optional<String> execute = execute(request);
      Optional<BidResponse> buy = JsonHelper.toEntity(execute.get(), BidResponse.class);
    } catch (Exception e) {
      log.error("Buy status error : " + e.getMessage());
    }
  }

  public boolean buy(AuctionInfo auctionInfo) {
    try {
      Long tradeId = auctionInfo.getTradeId();
      status(tradeId);
      log.info("Trying to bid : " + tradeId + " for " + auctionInfo.getBuyNowPrice());
      HttpPost request = createBidRequest(String.format(getBidUrl(), tradeId));
      Bid bid = new Bid((long) auctionInfo.getBuyNowPrice());
      String json = JsonHelper.toJsonString(bid);
      request.setEntity(new StringEntity(json));
      Optional<String> response = execute(request);
      if (!response.isPresent()) {
        return false;
      }
      Optional<BidResponse> buy = JsonHelper.toEntity(response.get(), BidResponse.class);
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

  public BidStatus makeBid(long tradeId, long bidPrice) {
    status(tradeId);
    log.info("Trying to bid : " + bidPrice + " for " + tradeId);
    HttpPost request = createBidRequest(String.format(getBidUrl(), tradeId));
    Bid bid = new Bid(bidPrice);
    String json = JsonHelper.toJsonString(bid);
    try {

      request.setEntity(new StringEntity(json));
      Optional<String> response = execute(request);
      Optional<TradeStatus> status = JsonHelper.toEntity(response.get(), TradeStatus.class);
      BidStatus bidStatus = new BidStatus();
      bidStatus.setTradeId(tradeId);
      TradeStatus tradeStatus = status.get();
      if (tradeStatus.getCode() == null) {
        bidStatus.setStatus(BidStatus.Status.OK);
      } else if (tradeStatus.getCode().equals(NOT_ALLOWED)) {
        log.debug("Not allowed for bid : " + tradeId + " Code : " + tradeStatus.getCode());
        bidStatus.setStatus(BidStatus.Status.FAIL);
        bidStatus.setErrorCode(tradeStatus.getCode());
      } else {
        log.error("Not defined code for bid : " + tradeId + " Code : " + tradeStatus.getCode());
        bidStatus.setStatus(BidStatus.Status.FAIL);
        bidStatus.setErrorCode(tradeStatus.getCode());
      }
      bidStatus.setInfo(tradeStatus);
      return bidStatus;
    } catch (Exception e) {
      log.error("Error bod for " + tradeId);
      return new BidStatus(BidStatus.Status.FAIL, ErrorCode.CATCH_ERROR, tradeId, null);
    }
  }

  /**
   * Used to sell item from transfer targets
   */
  public boolean item(Long itemId, Long tradeId) {
    SellItem toSell = new SellItem(itemId, tradeId);

    return executeItemRequest(toSell);
  }

  public boolean item(Long itemId) {
    SellItem toSell = new SellItem(itemId);

    return executeItemRequest(toSell);
  }

  private boolean executeItemRequest(SellItem toSell) {
    HttpPost request = createPutRequest(getItemUrl());

    try {
      request.setEntity(new StringEntity(JsonHelper.toJsonString(toSell)));
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage());
    }
    Optional<String> executionResult = execute(request);

    if (!executionResult.isPresent()) {
      return false;
    }
    String json = executionResult.get();
    FifaRequestStatus status = getStatus(json);

    if (status == FifaRequestStatus.FAILED) {
      return false;
    } else if (status == FifaRequestStatus.OK) {
      Optional<SellItem> entity = JsonHelper.toEntity(json, SellItem.class);
      return entity.isPresent();
    } else if (status == FifaRequestStatus.SESSION_EXPIRED) {
      return executeItemRequest(toSell);
    } else {
      return false;
    }
  }

  public Optional<AuctionHouseResponse> auctionHouse(Long id, int startPrice, int buyNow) {
    HttpPost request = createPostRequest(getAuctionHouseUrl());
    AuctionInfo toSell = new AuctionInfo();
    toSell.setStartingBid(startPrice);
    toSell.setBuyNowPrice(buyNow);
    toSell.setDuration(3600L);
    ItemData itemData = new ItemData();
    itemData.setId(id);
    toSell.setItemData(itemData);
    try {
      String json = JsonHelper.toJsonString(toSell);
      request.setEntity(new StringEntity(json));
      Optional<String> executionResult = execute(request);
      if (!executionResult.isPresent()) {
        return Optional.empty();
      }
      return JsonHelper.toEntity(executionResult.get(), AuctionHouseResponse.class);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return Optional.empty();
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


  public TradeStatus getTradeStatus(long tradeIds) {
    String url = String.format(getStatusUrl(), tradeIds);
    HttpPost request = createRequest(url);

    Optional<String> execute = execute(request);
    Optional<TradeStatus> status = JsonHelper.toEntity(execute.get(), TradeStatus.class);
    return status.get();
  }

  public void removeFromTargets(Long tradeId) {
    String url = String.format(FifaExternalEndpoints.WATCHLIST_WITH_IDS_URL, tradeId);
    HttpPost request = createDeleteRequest(url);
    execute(request);
  }

  public Optional<TradeStatus> search(MarketSearchRequest searchRequest) {
    String params = getSearchParameters(searchRequest);
    String url = String.format(FifaExternalEndpoints.FULL_SEARCH_URL, params);
    HttpPost request = createRequest(url);
    Optional<String> execute = execute(request);
    if (!execute.isPresent()) {
      log.error("Players not found");
      return Optional.empty();
    }
    String json = execute.get();
    if (isError(json)) {
      return Optional.empty();
    }
    return JsonHelper.toEntity(json, TradeStatus.class);

  }

  private String getSearchParameters(MarketSearchRequest searchRequest) {
    List<String> params = new ArrayList<>();
    params.add("type=player");
    params.add("num=12");
    params.add("start=" + searchRequest.getPage() * 12);
    if (searchRequest.getQuality() != null) {
      if (searchRequest.getQuality().equals("rare")) {
        params.add("rare=SP");
      } else {
        params.add("lev=" + searchRequest.getQuality());
      }
    }
    if (searchRequest.getMinPrice() != null) {
      params.add("micr=" + searchRequest.getMinPrice());
    }
    if (searchRequest.getMaxPrice() != null) {
      params.add("macr=" + searchRequest.getMaxPrice());
    }
    if (searchRequest.getMinBuyNowPrice() != null) {
      params.add("minb=" + searchRequest.getMinBuyNowPrice());
    }
    if (searchRequest.getMaxBuyNowPrice() != null) {
      params.add("maxb=" + searchRequest.getMaxBuyNowPrice());
    }
    if (searchRequest.getLeagueId() != null) {
      params.add("leag=" + searchRequest.getLeagueId());
    }
    if (searchRequest.getClubId() != null) {
      params.add("team=" + searchRequest.getClubId());
    }
    if (searchRequest.getNationId() != null) {
      params.add("nat=" + searchRequest.getNationId());
    }
    if (searchRequest.getPlayerId() != null) {
      params.add("maskedDefId=" + searchRequest.getPlayerId());
    }
    return String.join("&", params);
  }


}
