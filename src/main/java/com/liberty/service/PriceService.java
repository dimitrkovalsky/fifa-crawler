package com.liberty.service;

import com.liberty.model.PlayerStatistic;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
public interface PriceService {

    boolean isWorking();

    void findMinPriceAll();

    void updatePriceDistribution(boolean enabled);

    PlayerStatistic findMinPrice(long playerId);

    PlayerStatistic findMinPriceForSBC(long playerId);

    PlayerStatistic getMinPrice(Long id);

    void updatePrices();

    void updatePricesBigReward();

    void findMinPriceUnassigned();
}
