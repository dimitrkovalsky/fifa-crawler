package com.liberty.service;

import java.io.IOException;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:12
 */
public interface TradeService {

  void removeAllPlayers();

  void checkMarket();

  void addToAutoBuy(String name, long id, int maxPrice);

  void findMinPrice(long playerId) throws IOException;
}
