package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.service.PlayerService;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 20.05.2016.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

  @Override
  public List<PlayerProfile> getAllPlayers() {
    return null;
  }

  @Override
  public List<PlayerProfile> getAllPlayers(String source) {
    return null;
  }

  @Override
  public List<String> getAllSources() {
    return null;
  }
}
