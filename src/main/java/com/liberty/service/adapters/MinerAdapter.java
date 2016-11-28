package com.liberty.service.adapters;

import com.fifaminer.client.FifaMinerClient;
import com.fifaminer.client.dto.Duration;
import com.fifaminer.client.dto.OrderingTypeTO;
import com.fifaminer.client.dto.PlayerPriceTO;
import com.fifaminer.client.dto.strategy.MaxBuyStrategy;
import com.fifaminer.client.dto.strategy.SellStartStrategy;
import com.fifaminer.client.impl.FifaMinerClientBuilder;
import com.liberty.common.PriceHelper;
import com.liberty.model.MinerStrategy;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.strategy.AutomaticSellStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
@Slf4j
public class MinerAdapter {
    public static final int MINER_PORT = 3333;
    public static final int MIN_REWARD = 100;
    public static final int PLAYERS_FOR_AUTO_BUY = 25;
    private MaxBuyStrategy activeBuyStrategy;
    private SellStartStrategy activeSellStrategy;
    private FifaMinerClient client;

    @Autowired
    private PlayerProfileRepository profileRepository;

    @PostConstruct
    private void init() {
        client = new FifaMinerClientBuilder()
                .withProtocol("http")
                .withServerUrl("localhost")
                .withPort(MINER_PORT)
                .build();
        if (isAlive()) {
            enableStrategy(MaxBuyStrategy.REDUCE_15_FROM_CURRENT_MIN);
        }
    }

    private void enableStrategy(MaxBuyStrategy strategy) {
        client.enableMaxBuyPriceStrategy(strategy);
        activeBuyStrategy = strategy;
        log.info("Activated buy strategy : " + activeBuyStrategy.name());
    }

    private void enableStrategy(SellStartStrategy strategy) {
        client.enableSellStartPriceStrategy(strategy);
        activeSellStrategy = strategy;
        log.info("Activated sell strategy : " + activeSellStrategy.name());
    }

    public boolean isAlive() {
        return client.isHealthy();
    }

    public List<Long> getPlayersForUpdate() {
        return client.findPlayersByTransactionsAnalyse(Duration.LAST_2_DAYS, OrderingTypeTO.MIN_RELISTS, "miner",
                PLAYERS_FOR_AUTO_BUY)
                .stream()
                .filter(id -> !client.isPriceDistributionActual(id))
                .collect(Collectors.toList());
    }

    public List<PlayerTradeStatus> getPlayersToBuy() {
        List<Long> ids = client.findPlayersByTransactionsAnalyse(Duration.LAST_7_DAYS, OrderingTypeTO.MAX_SELLS, "miner",
                PLAYERS_FOR_AUTO_BUY);

        return ids.stream().map(this::getPricesToBuy).collect(Collectors.toList());
    }

    public PlayerTradeStatus getPricesToBuy(Long id) {
        PlayerPriceTO toBuy = client.getPricesSummary(id);
        PlayerProfile profile = profileRepository.findOne(toBuy.getPlayerId());
        PlayerTradeStatus status = new PlayerTradeStatus();
        status.setId(toBuy.getPlayerId());
        status.setName(profile.getName());
        status.setBuyPriceStrategy(toBuy.getMaxBuyPriceStrategy());
        status.setSellPriceStrategy(toBuy.getSellStartPriceStrategy());
        status.setMaxPrice(toBuy.getMaxBuyPrice());
        status.setSellStartPrice(toBuy.getSellStartPrice());
        return status;
    }

    public boolean shouldSellPlayer(Long playerId, Integer boughtFor) {
        return PriceHelper.calculateProfit(boughtFor, client.getSellStartPrice(playerId)) > 0;
    }

    public boolean isPriceDistributionActual(Long playerId) {
        return client.isPriceDistributionActual(playerId);
    }

    public AutomaticSellStrategy.MinerBid defineSellBid(Long playerId, Integer lastSalePrice) {
        return defineSellBid(playerId);
    }

    public AutomaticSellStrategy.MinerBid defineSellBid(Long playerId) {
        Integer sellBuyNowPrice = client.getSellBuyNowPrice(playerId);
        Integer sellStartPrice = client.getSellStartPrice(playerId);
        return new AutomaticSellStrategy.MinerBid(sellStartPrice, sellBuyNowPrice);
    }


    public List<MinerStrategy> getSellStrategies() {
        return Arrays.stream(SellStartStrategy.values())
                .map(strategy -> new MinerStrategy(strategy.ordinal(), strategy.getStrategyName(),
                        strategy == activeSellStrategy))
                .collect(Collectors.toList());
    }

    public void activateSellStrategy(Integer id) {
        enableStrategy(SellStartStrategy.values()[id]);
    }

    public void activateBuyStrategy(Integer id) {
        enableStrategy(MaxBuyStrategy.values()[id]);
    }

    public List<MinerStrategy> getBuyStrategies() {
        return Arrays.stream(MaxBuyStrategy.values())
                .map(strategy -> new MinerStrategy(strategy.ordinal(), strategy.getStrategyName(),
                        strategy == activeBuyStrategy))
                .collect(Collectors.toList());
    }
}
