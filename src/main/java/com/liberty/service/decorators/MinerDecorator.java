package com.liberty.service.decorators;

import com.liberty.model.PlayerTradeStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
@Component
public class MinerDecorator {
    public boolean isAlive() {
        return false;
    }

    public List<PlayerTradeStatus> getActivePlayers() {
        return null;
    }
}
