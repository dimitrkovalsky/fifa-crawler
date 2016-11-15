package com.liberty.service.adapters;

import com.fifaminer.client.FifaMinerClient;
import com.fifaminer.client.dto.OrderingTypeTO;
import com.fifaminer.client.dto.PlayerPriceTO;
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
        return true;
    }

    public List<PlayerTradeStatus> getActivePlayers() {
        long now = System.currentTimeMillis();
        long yesterday = now - 1000 * 60 * 60 * 24;
//        client.runForceTransactionAnalyse();
        List<PlayerPriceTO> result = client
                .findPlayersByTransactionsAnalyse(yesterday, now, OrderingTypeTO.MAX_SELLS, 10);
        return result.stream().map(x -> {
            PlayerProfile profile = profileRepository.findOne(x.getPlayerId());
            PlayerTradeStatus status = new PlayerTradeStatus();
            status.setId(x.getPlayerId());
            status.setName(profile.getName());
            status.setBidPriceStrategy(x.getBidPriceStrategy());
            status.setBuyPriceStrategy(x.getBuyPriceStrategy());
            status.setSellPriceStrategy(x.getSellPriceStrategy());
            status.setMaxPrice(x.getBuyPrice());
            status.setSellStartPrice(x.getSellPrice());
            return status;
        }).collect(Collectors.toList());
    }

    public boolean shouldSellPlayer(Long playerId, Integer boughtFor) {
        return false;
    }

    public AutomaticSellStrategy.MinerBid defineBid(Long id, Integer lastSalePrice) {
        return null;
    }
}
