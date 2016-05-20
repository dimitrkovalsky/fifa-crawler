package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Source;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.SourceRepository;
import com.liberty.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 20.05.2016.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

  @Autowired
  private SourceRepository sourceRepository;

  @Autowired
  private PlayerProfileRepository playerProfileRepository;

  @Override
  public List<PlayerProfile> getAllPlayers() {
    return playerProfileRepository.findAll();
  }

  @Override
  public List<PlayerProfile> getAllPlayers(String source) {
    return playerProfileRepository.findAllBySource(source);
  }

  @Override
  public List<Source> getAllSources() {
    return sourceRepository.findAll();
  }
}
