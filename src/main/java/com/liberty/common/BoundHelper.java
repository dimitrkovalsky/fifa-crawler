package com.liberty.common;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.PlayerStatistic;

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

  public static Integer defineLowBound(PlayerStatistic player, PlayerTradeStatus tradeStatus) {
    Integer lowBound;
    if (player.getLastPrice() == null) {
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
        lowBound = DEFAULT_LOW_BOUND;
      }
    } else {
      lowBound = tradeStatus.getMaxPrice();
    }
    if (player.getLastPrice() - tradeStatus.getMaxPrice() < 2000) {
      lowBound = player.getLastPrice();
    }
    return lowBound;
  }
}
