package com.liberty.service;

import com.liberty.model.PlayerFullInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Source;

import java.util.List;

/**
 * User: Dimitr Date: 19.05.2016 Time: 23:19
 */
public interface PlayerService {

  List<PlayerProfile> getAllPlayers();

  List<PlayerProfile> getAllPlayers(String source);

  List<Source> getAllSources();

  PlayerFullInfo geSingle(Long id);
}
