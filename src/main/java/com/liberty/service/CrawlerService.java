package com.liberty.service;

/**
 * User: Dimitr Date: 16.05.2016 Time: 21:44
 */
public interface CrawlerService {

  void fetchData(Long playerId);

  void saveOthers();

  void fetchAllPlayers();

  void fetchAllTrades();
}
