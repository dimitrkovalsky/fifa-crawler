package com.liberty.service.adapters;

import com.fifaminer.client.FifaMinerClient;
import com.fifaminer.client.dto.Duration;
import com.fifaminer.client.dto.OrderingTypeTO;
import com.fifaminer.client.dto.PlayerPriceTO;
import com.fifaminer.client.dto.SettingConfigurationTO;
import com.fifaminer.client.impl.FifaMinerClientBuilder;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.service.strategy.AutomaticSellStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
public class MinerAdapter {
    public static final int MINER_PORT = 3333;
    public static final int MIN_REWARD = 100;
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
    }

    public boolean isAlive() {
        return client.isHealthy();
    }

    public List<Long> getPlayersForUpdate() {
        return client.findPlayersByTransactionsAnalyse(Duration.TODAY, OrderingTypeTO.MIN_RELISTS, 10);
    }

    public List<PlayerTradeStatus> getPlayersToBuy() {
        long now = System.currentTimeMillis();
        long yesterday = now - 1000 * 60 * 60 * 24;

        List<Long> ids = client.findPlayersByTransactionsAnalyse(Duration.TODAY, OrderingTypeTO.MIN_RELISTS, 10);

        return ids.stream().map(id -> {
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
        }).collect(Collectors.toList());
    }

    public boolean shouldSellPlayer(Long playerId, Integer boughtFor) {
        return client.getSellBuyNowPrice(playerId) > (boughtFor + MIN_REWARD);
    }

    public boolean isPriceDistributionActual(Long playerId) {
        return false;
    }

    public AutomaticSellStrategy.MinerBid defineBid(Long id, Integer lastSalePrice) {
        Integer sellBuyNowPrice = client.getSellBuyNowPrice(id);
        Integer sellStartPrice = client.getSellStartPrice(id);
        return new AutomaticSellStrategy.MinerBid(sellStartPrice, sellBuyNowPrice);
    }

    public void changeStrategy(SettingConfigurationTO configuration) {
        client.updateSetting(configuration);
    }
}
