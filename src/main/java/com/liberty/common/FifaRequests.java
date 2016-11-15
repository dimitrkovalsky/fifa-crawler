package com.liberty.common;

import com.liberty.model.AuthRequest;
import com.liberty.model.AuthResponse;
import com.liberty.model.market.*;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.BidStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.liberty.common.ErrorCode.NOT_ALLOWED;
import static com.liberty.common.FifaCrawlerState.FAILED;
import static com.liberty.common.FifaEndpoints.AUTH;
import static com.liberty.common.UrlResolver.*;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests extends BaseFifaRequests {

    private Consumer<String> onError;

    private Consumer<FifaCrawlerState> onStateChange;

    private RequestResultProcessor processor = new RequestResultProcessor(this::updateSession);

    public FifaRequests(Consumer<String> onError, Consumer<FifaCrawlerState> onStateChange) {
        this.onError = onError;
        this.onStateChange = onStateChange;
    }

    public List<AuctionInfo> getTradePile() {
        HttpPost request = createRequest(getTradeLineUrl());
        Optional<String> executionResult = execute(request);

        return processor.processListResult(executionResult, TradeStatus.class,
                TradeStatus::getAuctionInfo, this::getTradePile);
    }

    public List<ItemData> getMyPlayers(int page) {
        String url = String.format(FifaExternalEndpoints.MY_PLAYERS, page * 96);
        HttpPost request = createRequest(url);
        Optional<String> executionResult = execute(request);

        return processor.processListResult(executionResult, Items.class,
                Items::getItemData, () -> getMyPlayers(page));
    }

    public Optional<Watchlist> getWatchlist() {
        HttpPost request = createRequest(getWatchlistUrl());
        Optional<String> execute = execute(request);

        return processor.processResult(execute, Watchlist.class, this::getWatchlist);
    }


    public Optional<TradeStatus> searchPlayer(long id, int maxPrice, int page) {
        HttpPost request = createRequest(String.format(getSearchUrl(), page, id, maxPrice));
        Optional<String> execute = execute(request);

        return processor.processResult(execute, TradeStatus.class, () -> searchPlayer(id, maxPrice,
                page));
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
            return false;
        }
        sessionId = auth.get();
        return true;
    }

    private String getAuthRequest() {
        AuthRequest request = new AuthRequest(2311254984L, NUCLEUS_PERSONA_ID);
        return JsonHelper.toJson(request).toString();
    }

    private Optional<String> auth() {
        try {
            HttpPost request = createAuthRequest(AUTH);
            String authRequest = getAuthRequest();
            StringEntity entity = new StringEntity(authRequest);
            entity.setContentType("application/json;charset=utf-8");
            log.info("Trying to retrieve new session... ");
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
        HttpPost request = createRequest(String.format(getStatusUrl(), tradeId));
        Optional<String> execute = execute(request);
        processor.processVoidResult(execute, () -> status(tradeId));
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
            return processor.processBooleanResult(response, BidResponse.class, () -> buy(auctionInfo));
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

        return processor.processBooleanResult(executionResult, SellItem.class, () ->
                executeItemRequest(toSell));
    }


    public Optional<AuctionHouseResponse> auctionHouse(Long id, int startPrice, int buyNow) {
        HttpPost request = createPostRequest(getAuctionHouseUrl());
        AuctionInfo toSell = createAuctionRequest(id, startPrice, buyNow);

        try {
            String json = JsonHelper.toJsonString(toSell);
            request.setEntity(new StringEntity(json));
            Optional<String> executionResult = execute(request);
            return processor.processResult(executionResult, AuctionHouseResponse.class,
                    () -> auctionHouse(id, startPrice, buyNow));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    private AuctionInfo createAuctionRequest(Long id, int startPrice, int buyNow) {
        AuctionInfo toSell = new AuctionInfo();
        toSell.setStartingBid(startPrice);
        toSell.setBuyNowPrice(buyNow);
        toSell.setDuration(3600L);
        ItemData itemData = new ItemData();
        itemData.setId(id);
        toSell.setItemData(itemData);
        return toSell;
    }


    public void removeAllSold() {
        HttpPost deleteRequest = createDeleteRequest(getRemoveSold());
        processor.processVoidResult(execute(deleteRequest), this::removeAllSold);
    }

    public void relistAll() {
        HttpPost putRequest = createPutRequest(getRelistUrl());
        processor.processVoidResult(execute(putRequest), this::relistAll);
    }

    public List<ItemData> getUnassigned() {
        HttpPost request = createRequest(getGetUnassignedUrl());

        Optional<String> executionResult = execute(request);

        return processor.processListResult(executionResult, Items.class, Items::getItemData,
                this::getUnassigned);
    }


    public Optional<TradeStatus> getTradeStatus(long tradeIds) {
        String url = String.format(getStatusUrl(), tradeIds);
        HttpPost request = createRequest(url);

        Optional<String> execute = execute(request);
        return processor.processResult(execute, TradeStatus.class, () -> getTradeStatus(tradeIds));
    }

    public void removeFromTargets(Long tradeId) {
        String url = String.format(FifaExternalEndpoints.WATCHLIST_WITH_IDS_URL, tradeId);
        HttpPost request = createDeleteRequest(url);
        processor.processVoidResult(execute(request), () -> removeFromTargets(tradeId));
    }

    public Optional<TradeStatus> search(MarketSearchRequest searchRequest) {
        String params = getSearchParameters(searchRequest);
        String url = String.format(FifaExternalEndpoints.FULL_SEARCH_URL, params);
        HttpPost request = createRequest(url);
        Optional<String> execute = execute(request);
        return processor.processResult(execute, TradeStatus.class, () -> search(searchRequest));
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
        if (searchRequest.getPosition() != null) {
            params.add("pos=" + searchRequest.getPosition());
        }
        return String.join("&", params);
    }


}
