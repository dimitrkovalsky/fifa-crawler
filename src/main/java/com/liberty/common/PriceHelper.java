package com.liberty.common;

import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;

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

  public static int getMinPrice(PlayerStatistic statistic) {
    if (statistic == null) {
      return 0;
    }
    statistic.getPrices().sort((o1, o2) -> o1.getPrice().compareTo(o2.getPrice()));
    if (!statistic.getPrices().isEmpty()) {
      return statistic.getPrices().get(0).getPrice();
    } else {
      return 0;
    }
  }
}
