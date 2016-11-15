package com.liberty.service;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Squad;

import java.util.List;

/**
 * User: Dimitr Date: 16.05.2016 Time: 21:44
 */
public interface CrawlerService {

    void fetchData(Long playerId);

    void saveOthers();

    void fetchAllPlayers();

    void fetchAllTrades();

    List<PlayerProfile> findProfilesBySquad(Long squadId);

    Squad fetchBaseSquadInfo(Long squadId);
}
