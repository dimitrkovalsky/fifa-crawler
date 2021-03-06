package com.liberty.service.impl;

import com.liberty.common.AuctionHouseResponse;
import com.liberty.common.DelayHelper;
import com.liberty.common.FifaCrawlerState;
import com.liberty.common.FifaRequests;
import com.liberty.model.RequestRate;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.TradeStatus;
import com.liberty.model.market.Watchlist;
import com.liberty.repositories.RequestRateRepository;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.request.TokenUpdateRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.RequestService;
import com.liberty.websockets.LogController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.liberty.common.FifaCrawlerState.*;

/**
 * @author Dmytro_Kovalskyi.
 * @since 26.10.2016.
 */
@Service
public class RequestServiceImpl implements RequestService {

    public static final int SEARCH_RATE_THRESHOLD = 30;
    public static final int RATE_THRESHOLD = 50;
    private volatile FifaCrawlerState currentState = TOKEN_NULL;

    private static long REQUEST_PER_DAY_LIMIT = 9500;

    @Autowired
    private LogController logController;

    @Autowired
    private RequestRateRepository requestRateRepository;

    private FifaRequests fifaRequests = new FifaRequests(this::onError, this::onStatusChange);

    private long ratePerDay = 0;

    private void onError(String msg) {
        logController.error(msg);
    }

    private void onStatusChange(FifaCrawlerState state) {
        currentState = state;
    }

    private int requestCount;
    private int searchRequestCount;

    private int rate = 0;
    private int searchRate = 0;
    private int arrayIndex = 0;
    private int[] rateArray = new int[30];
    private int[] searchRateArray = new int[30];

    private void logRequest() {
        requestCount++;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    private void resetRequestCount() {
        RequestRate requestRate = new RequestRate();
        requestRate.setRequestPerMinute(rate);
        requestRate.setSearchRequestPerMinute(searchRate);
        requestRate.setTimestamp(System.currentTimeMillis());
        requestRateRepository.save(requestRate);
        ratePerDay = computeRequestPerDay();
    }

    @Scheduled(fixedDelay = 2_000, initialDelay = 2_000)
    private void recordRequestCount() {
        rateArray[arrayIndex] = requestCount;
        searchRateArray[arrayIndex] = searchRequestCount;
        arrayIndex++;
        if (arrayIndex >= 30) {
            arrayIndex = 0;
        }
        rate = Arrays.stream(rateArray).sum();
        searchRate = Arrays.stream(searchRateArray).sum();
        logController.sendRate(rate);
        requestCount = 0;
        searchRequestCount = 0;
    }

    private <T> T execute(Supplier<T> function) {
        if (rateLimitExceeded())
            return null;
        waitReady();
        waitRateDecrease();
        currentState = WORKING;
        T result = function.get();
        logRequest();
        currentState = READY;
        return result;
    }

    private void waitRateDecrease() {
        long delay = 0;
        while (searchRate > SEARCH_RATE_THRESHOLD || rate > RATE_THRESHOLD) {
            int wait = 10;
            DelayHelper.waitStrict(wait);
            delay += wait;
            if (delay % 10_000 == 0) {
                logController.error("Request rate to high. " + getRateString());
            }
        }
    }

    private void execute(Runnable function) {
        if (rateLimitExceeded())
            return;
        waitReady();
        waitRateDecrease();
        currentState = WORKING;
        function.run();
        logRequest();
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

    private boolean rateLimitExceeded() {
//        if (ratePerDay >= REQUEST_PER_DAY_LIMIT) {
//            logController.error("RATE LIMIT EXCEEDED ==> " + requestCount);
//            return true;
//        }
        return false;
    }

    @Override
    public List<AuctionInfo> getTradePile() {
        return execute(() -> fifaRequests.getTradePile());
    }

    @Override
    public Watchlist getWatchlist() {
        Optional<Watchlist> result = execute(() -> fifaRequests.getWatchlist());
        return result.orElse(new Watchlist());
    }

    @Override
    public Optional<TradeStatus> searchPlayer(Long id, Integer maxPrice, int page) {
        return execute(() -> {
            searchRequestCount++;
            return fifaRequests.searchPlayer(id, maxPrice, page);
        });
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
    public List<ItemData> getMyPlayers() {
        return execute(() -> {
            int page = 0;
            List<ItemData> allPlayers = new ArrayList<>();
            List<ItemData> players;
            do {
                players = fifaRequests.getMyPlayers(page);
                allPlayers.addAll(players);
                page++;
                DelayHelper.wait(300, 20);
            } while (players.size() >= 96);
            return allPlayers;
        });
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

    private long computeRequestPerDay() {
        LocalDateTime failTime = LocalDateTime.now();
        LocalDateTime from = failTime.minus(24, ChronoUnit.HOURS);

        List<RequestRate> rates = requestRateRepository.findAllByTimestampBetween(toMillis(from), toMillis(failTime));
        return getOverallRate(rates);
    }

    private long getOverallRate(List<RequestRate> rates) {
        return rates.stream().mapToInt(RequestRate::getRequestPerMinute).sum();
    }

    private long toMillis(LocalDateTime from) {
        return from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public TradeStatus getTradeStatus(Long tradeId) {
        Optional<TradeStatus> execute = execute(() -> fifaRequests.getTradeStatus(tradeId));
        return execute.orElse(new TradeStatus());
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

    @Override
    public int getRequestRate() {
        return rate;
    }

    @Override
    public String getRateString() {
        return "Rate : " + getRequestRate() + ". Search : " + searchRate + ". Day : " + ratePerDay;
    }

    @Override
    public long getRequestRatePerDay() {
        return ratePerDay;
    }

}
