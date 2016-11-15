package com.liberty.service.impl;

import com.liberty.common.PriceHelper;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.liberty.common.PriceHelper.getHistoryPoint;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private PriceHistoryRepository historyRepository;

    @Override
    public void logPriceChange(Long playerId, List<PlayerStatistic.PriceDistribution> prices) {
        Map<Integer, Integer> priceDistribution = prices.stream()
                .collect(Collectors.toMap(PlayerStatistic.PriceDistribution::getPrice,
                        PlayerStatistic.PriceDistribution::getAmount));
        logPriceChange(playerId, priceDistribution);
    }

    @Override
    public void logPriceChange(Long playerId, Map<Integer, Integer> priceDistribution) {
        PriceHistory priceHistory = historyRepository.findOne(playerId);
        if (priceHistory == null) {
            priceHistory = new PriceHistory();
            priceHistory.setPlayerId(playerId);
        }
        priceHistory.getHistory().put(System.currentTimeMillis(), priceDistribution);
        historyRepository.save(priceHistory);
    }

    @Override
    public void logPriceChange(PlayerStatistic statistic) {

        logPriceChange(statistic.getId(), statistic.getPrices());
    }

    @Override
    public Map<Long, PriceHelper.HistoryPoint> getHistoryGraph(Long playerId) {
        PriceHistory history = historyRepository.findOne(playerId);
        if (history == null) {
            history = new PriceHistory();
            history.setPlayerId(playerId);
            historyRepository.save(history);
        }

        Set<Long> dates = history.getHistory().keySet();
        Map<Long, Map<Integer, Integer>> historyMap = history.getHistory();

        SortedMap<Long, PriceHelper.HistoryPoint> collectedHistory = new TreeMap<>();
        dates.forEach(d -> {
                    PriceHelper.HistoryPoint point = getHistoryPoint(historyMap.get(d));
                    collectedHistory.put(d, point);
                }
        );
        return collectedHistory;
    }


}
