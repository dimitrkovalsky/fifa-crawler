package com.liberty.service.strategy;

import com.liberty.common.BoundHelper;
import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.service.RequestService;
import com.liberty.websockets.LogController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.liberty.service.impl.TradeServiceImpl.ITERATION_LIMIT;
import static com.liberty.service.impl.TradeServiceImpl.STATISTIC_PLAYER_COLLECTION_AMOUNT;

/**
 * User: Dimitr
 * Date: 13.11.2016
 * Time: 19:20
 */
@Component
public class DefaultPriceUpdateStrategy implements PriceUpdateStrategy {

    public static final int DEFAULT_PRICE = 4000;

    @Autowired
    private PlayerStatisticRepository statisticRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private LogController logController;

    @Autowired
    private PlayerProfileRepository profileRepository;

    private static String PLATFORM = "pc";

    private int highBound;

    public Integer getFirsBound(long playerId) {
        PlayerStatistic statistic = statisticRepository.findOne(playerId);
        if (statistic == null)
            statistic = new PlayerStatistic();
        statistic.setId(playerId);
        statisticRepository.save(statistic);
        if (statistic.getPrices().isEmpty()) {
            return getDefaultPrice(statistic);
        }

        int count = 0;
        int bound = 0;
        for (PlayerStatistic.PriceDistribution distribution : statistic.getPrices()) {
            count += distribution.getAmount();
            bound = distribution.getPrice();
            if (count >= STATISTIC_PLAYER_COLLECTION_AMOUNT) {
                break;
            }
        }

        if (bound == 0) {
            bound = DEFAULT_PRICE; // TODO: fetch from history
        }

        PlayerProfile profile = profileRepository.findOne(playerId);
        int minPrice = profile.getPriceLimits().getPc().getMinPrice();        // TODO: platform dependent
        int maxPrice = profile.getPriceLimits().getPc().getMaxPrice();
        if (bound < minPrice)
            bound = minPrice;
        if (bound > maxPrice)
            bound = maxPrice;
        return bound;
    }

    private Integer getDefaultPrice(PlayerStatistic statistic) {
        return DEFAULT_PRICE;
    }


    @Override
    public Set<AuctionInfo> findPlayers(PlayerTradeStatus tradeStatus) {
        long playerId = tradeStatus.getId();
        int highBound = getFirsBound(playerId);
        int iteration = 0;
        Set<AuctionInfo> toStatistic = new HashSet<>();
        PlayerProfile profile = profileRepository.findOne(playerId);
        int maxPrice = profile.getPriceLimits().getPc().getMaxPrice();  // TODO: platform dependent
        boolean isHighBound = false;
        while (!isCompleted(toStatistic.size(), highBound, maxPrice)) {
            iteration++;
            logController.info("Trying to find " + tradeStatus.getName() + " less than " + highBound);

            List<AuctionInfo> players = findAllPlayers(playerId, highBound);

            toStatistic.addAll(players);
            logController.info("Found " + toStatistic.size() + " players");
            DelayHelper.wait(500, 20);
            highBound = BoundHelper.getHigherBound(0, highBound);
            if (highBound > maxPrice && !isHighBound) {
                highBound = maxPrice;
                isHighBound = true;
            }

            if (iteration >= ITERATION_LIMIT) {
                logController.info("Exceeded iteration limit");
                break;
            }
        }
        logController.info("Found " + toStatistic.size() + " players in " + iteration + " iterations");
        this.highBound = highBound;
        return toStatistic;
    }

    @Override
    public int getLastBound() {
        return highBound;
    }

    private List<AuctionInfo> findAllPlayers(long playerId, int lowBound) {
        List<AuctionInfo> players = findPlayerPage(playerId, lowBound, 0);

        if (players.size() == 0) {
            return players;
        } else if (players.size() >= 12) {
            players.addAll(findNextPagesPlayers(playerId, lowBound));
        }
        return players;
    }

    private boolean isCompleted(int size, Integer lowBound, int maxPrice) {
        return size >= STATISTIC_PLAYER_COLLECTION_AMOUNT || lowBound > maxPrice;
    }

    private List<AuctionInfo> findNextPagesPlayers(long playerId, Integer lowBound) {
        boolean completed = false;
        int page = 1;
        List<AuctionInfo> players = new ArrayList<>();
        while (!completed) {
            List<AuctionInfo> found = findPlayerPage(playerId, lowBound, page * 12);
            if (found.size() < 12) {
                completed = true;
            } else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            players.addAll(found);
            page++;
        }
        return players;
    }

    private List<AuctionInfo> findPlayerPage(long playerId, Integer lowBound, int page) {
        try {
            Optional<TradeStatus> maybe = requestService.searchPlayer(playerId, lowBound, page);
            return maybe.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
        } catch (Exception e) {
            logController.error(e.getMessage());
            return Collections.emptyList();
        }
    }

}
