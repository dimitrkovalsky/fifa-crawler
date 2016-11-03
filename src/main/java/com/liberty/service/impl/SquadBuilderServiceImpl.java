package com.liberty.service.impl;

import com.liberty.common.PriceHelper;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.Squad;
import com.liberty.model.SquadPlayer;
import com.liberty.service.CrawlerService;
import com.liberty.service.PriceService;
import com.liberty.service.SquadBuilderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

  @Override
  public Squad fetchPricesForSquad(Long squadId) {
    List<PlayerProfile> profilesBySquad = crawlerService.findProfilesBySquad(squadId);
    log.info("Found " + profilesBySquad.size() + " players for squad " + squadId);
    Squad squad = new Squad();
    squad.setSquadId(squadId);
    List<SquadPlayer> players = new ArrayList<>();
    int squadPrice = 0;
    for (PlayerProfile profile : profilesBySquad) {
      PlayerStatistic price = null; //priceService.findMinPrice(profile.getId());
     // DelayHelper.wait(7000, 777);
      int minPrice = PriceHelper.getMinPrice(price);
      squadPrice += minPrice;
      players.add(new SquadPlayer(profile.getId(), profile, minPrice));
    }
    squad.setPlayers(players);
    squad.setMinPrice(squadPrice);
    return squad;
  }
}
