package com.liberty.common;

import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: Dimitr
 * Date: 23.10.2016
 * Time: 13:55
 */
public class PriceHelper {

    public static final int DEFAULT_SILVER_PRICE = 800;
    public static final int DEFAULT_BRONZE_PRICE = 500;

    public static int defineMaxBuyNowPrice(Integer minMarketPrice) {
        if (minMarketPrice <= 1000) {
            return minMarketPrice - 200;
        } else if (minMarketPrice <= 2000) {
            return minMarketPrice - 300;
        } else if (minMarketPrice <= 3000) {
            return minMarketPrice - 400;
        } else if (minMarketPrice <= 4000) {
            return minMarketPrice - 500;
        } else if (minMarketPrice <= 5000) {
            return minMarketPrice - 700;
        } else if (minMarketPrice <= 7000) {
            return minMarketPrice - 1000;
        } else if (minMarketPrice <= 9000) {
            return minMarketPrice - 1500;
        } else if (minMarketPrice <= 12000) {
            return minMarketPrice - 2000;
        } else if (minMarketPrice <= 20000) {
            return minMarketPrice - 4000;
        } else if (minMarketPrice <= 30000) {
            return minMarketPrice - 5000;
        } else if (minMarketPrice <= 50000) {
            return minMarketPrice - 10000;
        } else if (minMarketPrice <= 70000) {
            return minMarketPrice - 20000;
        } else if (minMarketPrice <= 100000) {
            return minMarketPrice - 30000;
        } else {
            return minMarketPrice - 100000;
        }
    }

    public static int defineMaxBuyNowPriceBigReward(Integer minMarketPrice) {
        if (minMarketPrice <= 700) {
            return minMarketPrice;
        } else if (minMarketPrice <= 1000) {
            return minMarketPrice - 300;
        } else if (minMarketPrice <= 2000) {
            return minMarketPrice - 400;
        } else if (minMarketPrice <= 3000) {
            return minMarketPrice - 700;
        } else if (minMarketPrice <= 4000) {
            return minMarketPrice - 1000;
        } else if (minMarketPrice <= 5000) {
            return minMarketPrice - 1500;
        } else if (minMarketPrice <= 7000) {
            return minMarketPrice - 2000;
        } else if (minMarketPrice <= 9000) {
            return minMarketPrice - 3000;
        } else if (minMarketPrice <= 12000) {
            return minMarketPrice - 5000;
        } else if (minMarketPrice <= 20000) {
            return minMarketPrice - 7000;
        } else if (minMarketPrice <= 30000) {
            return minMarketPrice - 10000;
        } else if (minMarketPrice <= 50000) {
            return minMarketPrice - 20000;
        } else if (minMarketPrice <= 70000) {
            return minMarketPrice - 30000;
        } else if (minMarketPrice <= 100000) {
            return minMarketPrice - 35000;
        } else {
            return minMarketPrice - 100000;
        }
    }

    public static Integer defineMaxBuyNowPriceBigReward(Integer price, PlayerProfile profile) {
        Integer potentialPrice = defineMaxBuyNowPriceBigReward(price);
        if (profile == null) {
            return potentialPrice;
        }
        if (profile.getQuality().equals("silver") && potentialPrice > 1000) {
            return DEFAULT_SILVER_PRICE;
        }
        if (profile.getQuality().equals("bronze") && potentialPrice > 1000) {
            return DEFAULT_BRONZE_PRICE;
        }
        return potentialPrice;
    }

    public static Integer defineMaxBuyNowPrice(Integer price, PlayerProfile profile) {
        Integer potentialPrice = defineMaxBuyNowPrice(price);
        if (profile == null) {
            return potentialPrice;
        }
        if (profile.getQuality().equals("silver") && potentialPrice > 1000) {
            return DEFAULT_SILVER_PRICE;
        }
        if (profile.getQuality().equals("bronze") && potentialPrice > 1000) {
            return DEFAULT_BRONZE_PRICE;
        }
        return potentialPrice;
    }

    public static PlayerStatistic.PriceDistribution getMinPrice(PlayerStatistic statistic) {
        if (statistic == null) {
            return new PlayerStatistic.PriceDistribution(0, 0);
        }
        statistic.getPrices().sort((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()));
        if (!statistic.getPrices().isEmpty()) {
            return statistic.getPrices().get(0);
        } else {
            return new PlayerStatistic.PriceDistribution(0, 0);
        }
    }

    public static HistoryPoint getHistoryPoint(Map<Integer, Integer> priceDistribution) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        priceDistribution.forEach((k, v) -> {
            for (int i = 0; i < v; i++) {
                stats.addValue(k);
            }
        });

        return new HistoryPoint((long) stats.getMin(), (long) stats.getPercentile(50));
    }

    public static int calculateProfit(int boughtFor, int sellPrice) {
        return (int) (sellPrice - boughtFor - (sellPrice * 0.05));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoryPoint {

        private Long minPrice;
        private Long median;
    }

    public static Long getMedian(PlayerStatistic price) {

        HistoryPoint historyPoint = getHistoryPoint(price.getPrices().stream().collect(Collectors.toMap(
                PlayerStatistic.PriceDistribution::getPrice,
                PlayerStatistic.PriceDistribution::getAmount)));
        return historyPoint.getMedian();
    }
}
