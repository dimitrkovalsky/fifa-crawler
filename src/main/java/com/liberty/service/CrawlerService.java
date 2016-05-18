package com.liberty.service;

import com.liberty.model.PlayerProfile;

/**
 * User: Dimitr Date: 16.05.2016 Time: 21:44
 */
public interface CrawlerService {

  void execute();

  PlayerProfile fetchData(long playerId);
}
