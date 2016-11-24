package com.liberty.service.impl;

import com.liberty.listeners.ParameterUpdateListener;
import com.liberty.model.*;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.rest.request.AutobidRequest;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.ConfigService;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;
import com.liberty.service.UserParameterService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:11
 */
@Service
public class TradeServiceImpl extends ASellService implements TradeService,
        ApplicationListener<ContextRefreshedEvent>, ParameterUpdateListener {

    public static final int STATISTIC_PLAYER_COLLECTION_AMOUNT = 15;
    public static final int ITERATION_LIMIT = 30;

    private boolean autoBuyEnabled;

    // TODO: add rest to change active tag
    private Set<String> activeTags = new HashSet<>();

    @Autowired
    private PlayerStatisticRepository statisticRepository;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private PlayerProfileRepository profileRepository;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserParameterService parameterService;

    private boolean working = false;

    private void init() {
        activeTags = configService.getMarketConfig().getActiveTags();
        UserParameters userParameters = parameterService.getUserParameters();
        autoBuyEnabled = userParameters.isAutoBuyEnabled();
        autoSellRelistMinerEnabled = userParameters.isAutoSellRelistMinerEnabled();
        parameterService.subscribe(this);
    }

    @Override
    public void removeAllPlayers() {
        tradeRepository.deleteAll();
    }

    @Override
    public Set<String> getActiveTags() {
        return activeTags;
    }

    @Override
    public void disableAll() {
        tradeRepository.findAll().forEach(x -> {
            x.setEnabled(false);
            tradeRepository.save(x);
        });
    }

    @Override
    public void enablePlayer(Long id, Integer maxPrice, String tag) {
        PlayerTradeStatus status = tradeRepository.findOne(id);
        if (status == null) {
            logController.error("Can not find player for " + id);
            return;
        }
        status.setEnabled(true);
        status.setMaxPrice(maxPrice);
        status.addTag(tag);
        tradeRepository.save(status);
    }

    public void setActiveTags(Set<String> activeTags) {
        this.activeTags = activeTags;
    }


    @Override
    public boolean isActive() {
        return working;
    }

    @Override
    public void checkMarket() {
        if (working)
            return;
        if (isStopped())
            return;
        working = true;
        List<PlayerTradeStatus> players = tradeRepository.findAll().stream()
                .filter(filterPlayersToAutoBuy())
                .collect(Collectors.toList());
        Collections.shuffle(players, new Random(System.currentTimeMillis()));
        logController.info("Monitor : " + players.size() + " players" + ". " + requestService.getRateString());
        if (isEmpty(players)) {
            logController.info("Nothing to buy. Player trade is empty");
            working = false;
            return;
        }
        for (PlayerTradeStatus p : players) {
            p = tradeRepository.findOne(p.getId()); // to have last price
            logController.info("Trying to check " + p.getName() + " max price => " + p.getMaxPrice() + ". " +
                    requestService.getRateString());
            if (!autoBuyEnabled) {
                working = false;
                return;
            }
            PlayerProfile profile = profileRepository.findOne(p.getId());
            if (p.getMaxPrice() < profile.getPriceLimits().getPc().getMinPrice())
                continue;
            boolean success = checkMarket(p);
            logController.info("Total purchases : " + purchases);
            if (purchases >= maxPurchaseAmount) {
                logController.info("Limit of purchases : " + purchases);
                onFailed();
                working = false;
                return;
            }
            sleep();
        }
        working = false;
    }

    private boolean isStopped() {
        if (!autoBuyEnabled) {
            logController.info("Auto Buy Disabled...");
            return true;
        }
        if (purchases >= maxPurchaseAmount) {
            logController.info(
                    "MAX purchase amount is : " + maxPurchaseAmount + " currently bought " + purchases);
            return true;
        }
        return false;
    }

    private Predicate<PlayerTradeStatus> filterPlayersToAutoBuy() {
        return p -> {
//            if (!activeTags.isEmpty()) {
//                return p.isEnabled() && !CollectionUtils.intersection(p.getTags(), activeTags).isEmpty();
//            }
            return p.isEnabled();
        };
    }

    private boolean checkMarket(PlayerTradeStatus playerTradeStatus) {
        try {
            sleep();

            Optional<TradeStatus> maybe = requestService.searchPlayer(playerTradeStatus.getId(),
                    playerTradeStatus.getMaxPrice(), 0);
            if (!maybe.isPresent()) {
                return false;
            }
            TradeStatus tradeStatus = maybe.get();
            int found = tradeStatus.getAuctionInfo().size();
            logController
                    .info("Found " + found + " players for " + playerTradeStatus.getName() + " maxPrice : "
                            + playerTradeStatus.getMaxPrice());
            if (found <= 0) {
                return true;
            }
            buyPlayers(tradeStatus, playerTradeStatus);
        } catch (Exception e) {
            logController.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean buyPlayer(long playerId, int maxPrice, String playerName) {
        Optional<TradeStatus> maybe = requestService.searchPlayer(playerId, maxPrice, 0);
        if (!maybe.isPresent()) {
            return false;
        }
        TradeStatus tradeStatus = maybe.get();
        int found = tradeStatus.getAuctionInfo().size();
        logController.info("Found " + found + " players for " + playerName + " maxPrice : " + maxPrice);
        if (found <= 0) {
            return false;
        }

        List<AuctionInfo> list = foundMinList(tradeStatus);
        if (list.isEmpty()) {
            logController.error("Can not find trades for " + playerName);
            return false;
        }
        boolean success = requestService.buy(list.get(0));
        if (success) {
            logController.info("Success bought player for " + playerName + ". " + requestService.getRateString());
        } else {
            logController.error("Can not buy player for " + playerName);
        }
        return success;
    }

    @Override
    public void addToAutoBuy(String name, long id, int maxPrice) {
        PlayerTradeStatus status = new PlayerTradeStatus(id, name, maxPrice);
        status.updateDate();
        tradeRepository.save(status);
    }

    @Override
    public MarketInfo getMarketInfo() {
        MarketInfo info = new MarketInfo();
        info.setMaxPurchases(maxPurchaseAmount);
        info.setPhishingToken(requestService.getPhishingTokenForCheck());
        info.setSessionId(requestService.getSessionForCheck());
        info.setAutoBuyEnabled(autoBuyEnabled);
        info.setRate(requestService.getRequestRate());
        return info;
    }

    @Override
    public void autoBuy(AutobuyRequest request) {
        if (request.getEnabled() == null) {
            return;
        }
        this.autoBuyEnabled = request.getEnabled();
        UserParameters userParameters = parameterService.getUserParameters();
        userParameters.setAutoBuyEnabled(autoBuyEnabled);
        parameterService.saveParameters(userParameters);
        if (autoBuyEnabled) {
            purchases = 0;
            failed = false;
            maxPurchaseAmount = request.getPurchases();
            checkMarket();
        }
    }

    @Override
    public void deleteFromAutoBuy(Long id) {
        statisticRepository.delete(id);
    }


    @Override
    public List<PlayerInfo> getAllToAutoBuy() {
        List<PlayerTradeStatus> all = tradeRepository.findAll();


        Map<Long, PlayerStatistic> idMinPrice = getStatsMap();
        List<Long> ids = all.stream().map(PlayerTradeStatus::getId).collect(Collectors.toList());
        List<PlayerProfile> profiles = playerProfileService.getAll(ids);
        Map<Long, PlayerProfile> profileMap = new HashMap<>();
        profiles.forEach(p -> profileMap.put(p.getId(), p));

        List<PlayerInfo> infos = new ArrayList<>();
        all.forEach(p -> {
            PlayerStatistic stats = idMinPrice.get(p.getId());
            if (stats != null) {
                List<PlayerStatistic.PriceDistribution> prices = stats.getPrices();
                if (prices != null && !prices.isEmpty()) {
                    p.setMinMarketPrice(prices.get(0).getPrice());
                }
                p.setLastUpdate(stats.getDate());
                infos.add(new PlayerInfo(p, profileMap.get(p.getId())));
            }
        });


        return infos;
    }

    @Override
    public List<PlayerInfo> getAllToAutoBuy(Set<String> tags) {
        return getAllToAutoBuy().stream().filter(x -> !containsTag(tags, x))
                .collect(Collectors.toList());
    }

    private boolean containsTag(Set<String> tags, PlayerInfo playerInfo) {
        return CollectionUtils
                .isEmpty(CollectionUtils.intersection(playerInfo.getTradeStatus().getTags(),
                        tags));
    }


    private Map<Long, PlayerStatistic> getStatsMap() {
        List<PlayerStatistic> stats = statisticRepository.findAll();
        Map<Long, PlayerStatistic> idMinStats = new HashMap<>();
        stats.forEach(s -> {
            idMinStats.put(s.getId(), s);
        });
        return idMinStats;
    }

    @Override
    public PlayerInfo getPlayerInfo(Long id) {
        PlayerTradeStatus tradeStatus = tradeRepository.findOne(id);
        PlayerProfile profile = playerProfileService.findOne(id);
        return new PlayerInfo(tradeStatus, profile);
    }

    @Override
    public void updateAutoBuy(BuyRequest request) {
        PlayerTradeStatus playerTradeStatus = tradeRepository.findOne(request.getId());
        playerTradeStatus.setEnabled(request.getEnabled());
        playerTradeStatus.updateDate();
        tradeRepository.save(playerTradeStatus);
    }

    @Override
    public void updatePlayer(PlayerTradeStatus request) {
        PlayerTradeStatus toUpdate = tradeRepository.findOne(request.getId());
        if (toUpdate == null) {
            toUpdate = createNewTrade(request);
        }
        toUpdate.setSellStartPrice(request.getSellStartPrice());
        toUpdate.setSellBuyNowPrice(request.getSellBuyNowPrice());
        toUpdate.setMaxPrice(request.getMaxPrice());
        toUpdate.updateDate();
        tradeRepository.save(toUpdate);
    }

    @Override
    public List<AuctionInfo> getTransferTargets() {
        return requestService.getWatchlist().getAuctionInfo();
    }

    @Override
    public void removeExpired(List<AuctionInfo> expired) {
        expired.forEach(x -> removeFromTargets(x.getTradeId()));
    }

    @Override
    public void removeFromTargets(Long tradeId) {
        requestService.removeFromTargets(tradeId);
    }

    @Override
    public TradeStatus getTradeStatus(Long tradeId) {
        return requestService.getTradeStatus(tradeId);
    }

    @Override
    public BidStatus makeBid(Long tradeId, Long price) {
        BidStatus bidStatus = requestService.makeBid(tradeId, price);

        System.out.println("Bid response status: " + bidStatus.getStatus());
        return bidStatus;
    }

    private PlayerTradeStatus createNewTrade(PlayerTradeStatus request) {
        PlayerProfile profile = playerProfileService.findOne(request.getId());
        PlayerTradeStatus tradeStatus = new PlayerTradeStatus();
        tradeStatus.setId(request.getId());
        tradeStatus.setName(profile.getName());
        tradeStatus.setEnabled(false);
        return tradeStatus;
    }

    @Override
    public void addToAutoBid(AutobidRequest bidRequest) {
        Long playerId = bidRequest.getPlayerId();
        PlayerTradeStatus playerTradeStatus = tradeRepository.findOne(playerId);
        if (playerTradeStatus == null) {
            playerTradeStatus = createNewTrade(bidRequest);
        }
        logController.info("Added to autobid " + playerTradeStatus.getName() + ". Max price : " +
                playerTradeStatus.getMaxPrice());
    }

    private PlayerTradeStatus createNewTrade(AutobidRequest bidRequest) {
        Long playerId = bidRequest.getPlayerId();
        PlayerProfile profile = playerProfileService.findOne(playerId);
        PlayerTradeStatus tradeStatus = new PlayerTradeStatus();
        tradeStatus.setId(playerId);
        tradeStatus.setName(profile.getName());
        tradeStatus.setMaxPrice(bidRequest.getMaxBid());
        tradeRepository.save(tradeStatus);
        return tradeStatus;
    }

    @Override
    protected Integer getPurchasesRemained() {
        return maxPurchaseAmount - purchases;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        init();
    }

    @Override
    public void onParameterUpdate(ParameterUpdateRequest request) {
        if (request.getAutoBuyEnabled() != null)
            autoBuyEnabled = request.getAutoBuyEnabled();

        if (request.getAutoSellRelistMinerEnabled() != null)
            autoSellRelistMinerEnabled = request.getAutoSellRelistMinerEnabled();
    }
}
