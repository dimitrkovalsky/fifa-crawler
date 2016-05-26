package com.liberty.service.impl;

import com.liberty.model.PlayerFullInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PriceHistory;
import com.liberty.model.Source;
import com.liberty.repositories.PlayerMonitoringRepository;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.repositories.SourceRepository;
import com.liberty.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 20.05.2016.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

  @Autowired
  private SourceRepository sourceRepository;

  @Autowired
  private PlayerMonitoringRepository playerMonitoringRepository;

  @Autowired
  private PlayerProfileRepository playerProfileRepository;

  @Autowired
  private PriceHistoryRepository historyRepository;

  @Override
  public List<PlayerProfile> getAllPlayers() {
    return filterBySource(playerProfileRepository::findAll);
  }

  @Override
  public List<PlayerProfile> getAllPlayers(String source) {
    return filterBySource(() -> playerProfileRepository.findAllBySource(source));
  }

  private List<PlayerProfile> filterBySource(Supplier<List<PlayerProfile>> supplier) {
    Map<Long, PlayerProfile> profileMap = supplier.get().stream()
        .collect(Collectors.toMap(PlayerProfile::getId, p -> p));
    playerMonitoringRepository.findAll().forEach(mon -> {
      PlayerProfile playerProfile = profileMap.get(mon.getId());
      if (playerProfile != null)
        playerProfile.setUnderMonitoring(true);
    });
    return profileMap.values().stream().collect(Collectors.toList());
  }

  @Override
  public List<Source> getAllSources() {
    return sourceRepository.findAll();
  }

  @Override
  public PlayerFullInfo geSingle(Long id) {
    if(id == null)
      throw new IllegalArgumentException("Id can not be null");
    PlayerProfile profile = playerProfileRepository.findOne(id);
    PriceHistory history = historyRepository.findOne(id);
    PlayerFullInfo info = new PlayerFullInfo();
    info.setProfile(profile);
    info.setHistory(history);
    return info;
  }
}
