package com.liberty.common;

/**
 * User: Dimitr
 * Date: 23.10.2016
 * Time: 13:55
 */
public class PriceHelper {

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
}
