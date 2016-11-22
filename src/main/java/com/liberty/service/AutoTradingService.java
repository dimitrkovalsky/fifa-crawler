package com.liberty.service;

import com.liberty.model.MinerStrategy;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.11.2016.
 */
public interface AutoTradingService {

    void updateActivePlayers();

    void checkUpdates();

    List<MinerStrategy> getBuyStrategies();

    void activateBuyStrategy(Integer id);

    void activateSellStrategy(Integer id);

    List<MinerStrategy> getSellStrategies();
}
