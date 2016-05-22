package com.liberty.service;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;

/**
 * User: Dimitr Date: 16.05.2016 Time: 21:44
 */
public interface CrawlerService {

  void execute();

  void monitorInforms();

  PlayerProfile fetchData(long playerId, boolean force);

  Price getCurrentPrice(Long id);

  void fetchTots();

  void fetchTows();

  void fetchSources();
}
