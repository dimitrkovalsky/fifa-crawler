package com.liberty.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.FifaError;
import com.liberty.model.market.TradeStatus;

import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.FifaEndpoints.SEARCH_URL;
import static com.liberty.common.FifaEndpoints.TRADE_LINE_URL;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests extends BaseFifaRequests {


  public void getTradeLine() {
    HttpPost request = createRequest(TRADE_LINE_URL);
    Optional<String> result = execute(request);
    System.out.println(result);
  }

  public TradeStatus searchPlayer(long id, int maxPrice) throws IOException {
    HttpPost request = createRequest(String.format(SEARCH_URL, maxPrice, id));
    Optional<String> execute = execute(request);
    if (!execute.isPresent()) {
      log.error("Player not found");
      return null;
    }
    String json = execute.get();
    if (isError(json)) {
      return null;
    }
    TradeStatus tradeStatus = (TradeStatus) JsonHelper.toEntity(json, TradeStatus.class).get();
    return tradeStatus;
  }

  public Optional<AuctionInfo> foundMin(TradeStatus tradeStatus) {
    // Check contract=0
    Optional<AuctionInfo> min = tradeStatus.getAuctionInfo().stream()
        .min((a1, a2) -> a1.getBuyNowPrice().compareTo(a2.getBuyNowPrice()));
    return min;
  }

  public boolean isError(String json) {
    ObjectMapper objectMapper = JsonHelper.getObjectMapper();
    try {
      FifaError fifaError = objectMapper.readValue(json, FifaError.class);
      log.error("FIFA ERROR : " + fifaError.getReason());
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  public ObjectMapper getMapper() {
    return new ObjectMapper();
  }

  public static void main(String[] args) throws IOException {
    //208830 - vardy
    FifaRequests fifaRequests = new FifaRequests();
    TradeStatus tradeStatus = fifaRequests.searchPlayer(208830L, 1000);
    Optional<AuctionInfo> auctionInfo = fifaRequests.foundMin(tradeStatus);
    System.out.println(auctionInfo);
  }

  protected String getSessionId() {
    return "a0a64209-eed0-4e52-a0f3-9537d0bece29";
  }

  protected String getPhishingToken() {
    return "6330608281155395459";
  }
}
