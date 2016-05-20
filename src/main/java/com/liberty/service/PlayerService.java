package com.liberty.service;

import com.liberty.model.PlayerProfile;

import java.util.List;

/**
 * User: Dimitr Date: 19.05.2016 Time: 23:19
 */
public interface PlayerService {

  List<PlayerProfile> getAllPlayers();

  List<PlayerProfile> getAllPlayers(String source);

  List<String> getAllSources();
}
