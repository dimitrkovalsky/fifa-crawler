package com.liberty.service.impl;

import com.liberty.common.DateHelper;
import com.liberty.common.DelayHelper;
import com.liberty.common.PriceHelper;
import com.liberty.model.FullSquad;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.Squad;
import com.liberty.model.SquadPlayer;
import com.liberty.model.SquadStatistic;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.SquadRepository;
import com.liberty.repositories.SquadStatisticRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.PriceService;
import com.liberty.service.SquadBuilderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Service
@Slf4j
public class SquadBuilderServiceImpl implements SquadBuilderService {

  @Autowired
  private CrawlerService crawlerService;

  @Autowired
  private PriceService priceService;

  @Autowired
  private SquadRepository squadRepository;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private SquadStatisticRepository squadStatisticRepository;

  @Override
  public FullSquad fetchPricesForSquad(Long squadId) {
    Squad stored = squadRepository.findOne(squadId);
    if (stored != null) {
      return useStoredSquad(stored);
    }

    return updateSquad(squadId);
  }

  @Override
  public FullSquad updateSquad(Long squadId) {
    List<PlayerProfile> profilesBySquad = crawlerService.findProfilesBySquad(squadId);
    log.info("Found " + profilesBySquad.size() + " players for squad " + squadId);
    FullSquad fullSquad = new FullSquad();
    fullSquad.setSquadId(squadId);
    List<SquadPlayer> players = new ArrayList<>();
    int squadPrice = 0;
    for (PlayerProfile profile : profilesBySquad) {
      PlayerStatistic price = priceService.findMinPrice(profile.getId());
      PlayerStatistic.PriceDistribution minPrice = PriceHelper.getMinPrice(price);
      squadPrice += minPrice.getPrice();
      players.add(new SquadPlayer(profile.getId(), profile, minPrice));
      DelayHelper.wait(7000, 777);
    }
    fullSquad.setPlayers(players);
    fullSquad.setPrice(new HistoryServiceImpl.HistoryPoint((long) squadPrice, 0L));
    fullSquad.setDate(DateHelper.toReadableString(LocalDateTime.now()));
    saveSquad(fullSquad);
    updateHistory(fullSquad);
    return fullSquad;
  }

  private void updateHistory(FullSquad fullSquad) {
    Long squadId = fullSquad.getSquadId();
    SquadStatistic statistic = squadStatisticRepository.findOne(squadId);
    if (statistic == null) {
      statistic = new SquadStatistic();
      statistic.setId(squadId);
    }

    statistic.getHistory().put(System.currentTimeMillis(), fullSquad.getPrice());
  }

  @Override
  public void buySquad(Long squadId) {
    Squad squad = squadRepository.findOne(squadId);
    if (squad == null) {
      log.error("Can not find squad with id : " + squadId);
      return;
    }

  }

  private void saveSquad(FullSquad fullSquad) {
    Squad squad = new Squad();
    squad.setId(fullSquad.getSquadId());
    squad.setInnerDate(LocalDateTime.now());
    List<Long> ids = fullSquad.getPlayers().stream()
        .map(SquadPlayer::getPlayerId)
        .collect(Collectors.toList());
    squad.setPlayerIds(ids);
    squadRepository.save(squad);
  }

  private FullSquad useStoredSquad(Squad stored) {
    FullSquad squad = new FullSquad();
    squad.setSquadId(stored.getId());
    int squadPrice = 0;
    List<SquadPlayer> players = new ArrayList<>();
    for (Long id : stored.getPlayerIds()) {
      PlayerProfile profile = profileRepository.findOne(id);
      PlayerStatistic minPrice = priceService.getMinPrice(id);
      PlayerStatistic.PriceDistribution minDistribution = PriceHelper.getMinPrice(minPrice);
      players.add(new SquadPlayer(id, profile, minDistribution));
      squadPrice += minDistribution.getPrice();
    }
    squad.setPlayers(players);
    squad.setPrice(new HistoryServiceImpl.HistoryPoint((long) squadPrice, 0L));
    squad.setDate(stored.getDate());
    return squad;
  }
}
