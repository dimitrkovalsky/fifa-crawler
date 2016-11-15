package com.liberty.service.impl;

import com.liberty.model.PlayerStatistic;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.Statistic;
import com.liberty.model.market.AuctionInfo;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.HistoryService;
import com.liberty.service.RequestService;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;
import com.liberty.websockets.BuyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.liberty.common.Comparators.getAuctionInfoComparator;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private PlayerTradeStatusRepository statusRepository;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PlayerStatisticRepository statisticRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RequestService requestService;

    @Override
    public Statistic getGeneralStatistic() {
        Statistic statistic = new Statistic();
        statistic.setPlayers(statusRepository.count());
        statistic.setAutoBuy(statusRepository.findAll().stream()
                .filter(PlayerTradeStatus::isEnabled)
                .count());
        statistic.setEnabled(tradeService.getMarketInfo().getAutoBuyEnabled());
        BuyMessage tradepileInfo = tradeService.getTradepileInfo();
        statistic.setCredits(tradepileInfo.getCredits());
        statistic.setRate(requestService.getRequestRate());
        return statistic;
    }

    @Override
    public void collectStatistic(long playerId, int lowBound, Set<AuctionInfo> toStatistic) {
        if (toStatistic.isEmpty()) {
            updateStatistic(playerId, lowBound);
        }
        Map<Integer, List<AuctionInfo>> stats =
                toStatistic.stream().collect(Collectors.groupingBy(AuctionInfo::getBuyNowPrice));
        updateStats(playerId, toStatistic.stream().collect(Collectors.toList()), stats);
    }

    private PlayerStatistic updateStatistic(long playerId, Integer lowBound) {
        PlayerStatistic toSave = new PlayerStatistic();
        toSave.setId(playerId);
        toSave.setLastPrice(lowBound);
        toSave.setInnerDate(LocalDateTime.now());
        statisticRepository.save(toSave);
        historyService.logPriceChange(toSave);
        return toSave;
    }

    private void updateStats(long playerId, List<AuctionInfo> toStatistic,
                             Map<Integer, List<AuctionInfo>> stats) {
        foundMin(toStatistic).ifPresent(m -> {
            PlayerStatistic toSave = new PlayerStatistic();
            toSave.setId(playerId);
            toSave.setLastPrice(m.getBuyNowPrice());
            List<PlayerStatistic.PriceDistribution> prices = toStatistic(stats);
            prices = prices.stream()
                    .sorted(Comparator.comparing(PlayerStatistic.PriceDistribution::getPrice))
                    .collect(Collectors.toList());
            toSave.setPrices(prices);
            toSave.setInnerDate(LocalDateTime.now());
            statisticRepository.save(toSave);
            historyService.logPriceChange(toSave);
        });
    }

    private Optional<AuctionInfo> foundMin(List<AuctionInfo> statuses) {
        Optional<AuctionInfo> min = statuses.stream()
                .min(getAuctionInfoComparator());
        return min.flatMap(m -> {
            if (m.getItemData().getContract() <= 0) {
                return Optional.empty();
            }
            return Optional.of(m);
        });
    }

    private List<PlayerStatistic.PriceDistribution> toStatistic(
            Map<Integer, List<AuctionInfo>> stats) {
        List<PlayerStatistic.PriceDistribution> result = new ArrayList<>();
        stats.forEach((k, v) -> {
            result.add(new PlayerStatistic.PriceDistribution(k, v.size()));
        });
        return result;
    }
}
