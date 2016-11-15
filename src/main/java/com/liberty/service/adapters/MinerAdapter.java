package com.liberty.service.adapters;

import com.fifaminer.client.FifaMinerClient;
import com.fifaminer.client.impl.FifaMinerClientBuilder;
import com.liberty.model.PlayerTradeStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
public class MinerAdapter {
    public static final int MINER_PORT = 3333;
    private FifaMinerClient client;


    private void init() {
        client = new FifaMinerClientBuilder()
                .withProtocol("http")
                .withServerUrl("localhost")
                .withPort(MINER_PORT)
                .build();
    }

    public boolean isAlive() {
        return false;
    }

    public List<PlayerTradeStatus> getActivePlayers() {
        return null;
    }
}
