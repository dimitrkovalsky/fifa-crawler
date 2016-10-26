package com.liberty.common;

import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.PlayerStatistic;

import static com.liberty.service.impl.TradeServiceImpl.DEFAULT_LOW_BOUND;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
public class BoundHelper {

  public static int getHigherBound(int found, Integer lowBound) {
    if (lowBound < 1000) {
      return lowBound + 50;
    } else if (lowBound <= 3000) {
      return lowBound + 100;
    } else if (lowBound <= 5000) {
      return lowBound + 300;
    } else if (lowBound < 10000) {
      return lowBound + 500;
    } else if (lowBound < 50000) {
      return lowBound + 1500;
    } else if (lowBound < 100000) {
      return lowBound + 2500;
    } else {
      return lowBound + 5000;
    }
  }

  public static Integer getLowerBound(Integer lowBound) {
    if (lowBound <= 1000) {
      return lowBound - 50;
    } else if (lowBound <= 10000) {
      return lowBound - 100;
    } else if (lowBound <= 50000) {
      return lowBound - 250;
    } else if (lowBound <= 100000) {
      return lowBound - 500;
    } else {
      return lowBound - 1000;
    }
  }

  public static Integer defineLowBound(PlayerStatistic stats, PlayerTradeStatus tradeStatus) {

    Integer lowBound;

    if (stats.getLastPrice() == null) {
      if (tradeStatus.getMaxPrice() > 50000) {
        lowBound = 50000;
      } else if (tradeStatus.getMaxPrice() > 30000) {
        lowBound = 30000;
      } else if (tradeStatus.getMaxPrice() > 10000) {
        lowBound = 10000;
      } else if (tradeStatus.getMaxPrice() > 5000) {
        lowBound = 5000;
      } else if (tradeStatus.getMaxPrice() > 3000) {
        lowBound = 3000;
      } else {
        return DEFAULT_LOW_BOUND;
      }
    } else if (stats.getLastPrice() - tradeStatus.getMaxPrice() >= 2000) {
      lowBound = stats.getLastPrice();
    } else if (tradeStatus.getMaxPrice() <= 5000) {
      lowBound = tradeStatus.getMaxPrice();
    } else {
      lowBound = tradeStatus.getMaxPrice();
    }
    return 10000;
//    return lowBound;
  }

  public static Integer defineMaxBuyNow(PlayerProfile profile) {
    Integer rating = profile.getRating();
    int maxBuy;
    if (profile.isSpecialType) {
      maxBuy = 50000;
    } else if (rating >= 85) {
      maxBuy = 20000;
    } else if (rating >= 82) {
      maxBuy = 2000;
    } else {
      maxBuy = 1000;
    }
    return maxBuy;
  }

  public static int defineNextBid(Integer currentBid) {
    if (currentBid <= 0) {
      return 150;
    } else if (currentBid < 1000) {
      return currentBid + 50;
    } else if (currentBid < 10_000) {
      return currentBid + 100;
    } else if (currentBid < 50_000) {
      return currentBid + 250;
    } else if (currentBid < 100_000) {
      return currentBid + 500;
    } else {
      return currentBid + 1000;
    }
  }
}
