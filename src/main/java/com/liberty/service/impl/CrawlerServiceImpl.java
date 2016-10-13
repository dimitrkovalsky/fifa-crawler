package com.liberty.service.impl;

import com.liberty.model.FifaPlayerSuggestion;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.processors.FifaDatabaseProcessor;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Component
@Slf4j
public class CrawlerServiceImpl implements CrawlerService {

  @Autowired
  private FifaDatabaseProcessor processor;

  @Autowired
  private PlayerProfileRepository profileRepository;


  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Autowired
  private ImageService imageService;

  @Override
  public void fetchData(Long playerId) {
    PlayerProfile profile = profileRepository.findOne(playerId);
    // TODO: Fix club and nation fetch if profile was saved;
    if (profile == null) {
      Optional<PlayerProfile> maybeProfile = processor.fetchInfo(playerId);
      if (!maybeProfile.isPresent()) {
        log.error("Can not fetch data for playerId : " + playerId);
        return;
      }
      profile = maybeProfile.get();
    } else {
      log.info("Player profile with id : " + playerId + " already exists");
    }
    profileRepository.save(profile);
    imageService.saveImage(profile.getHeadshotImgUrl(), playerId);


  }

  private void saveOthers() {

  }

  @Override
  public void fetchAllPlayers() {
    List<FifaPlayerSuggestion> suggestions = processor.readSuggestions();
    List<FifaPlayerSuggestion> players = suggestions;
//    suggestions.stream()
//        .filter(x -> x.getRating() >= 80)
//        .collect(Collectors.toList());

    log.info("Trying to fetch data for : " + players.size() + " players");
    AtomicInteger counter = new AtomicInteger(0);
    players.parallelStream().forEach(p -> {
      fetchData(p.getId());
      log.info("Fetched " + counter.incrementAndGet() + " / " + players.size());
    });
    saveOthers();
  }

  @Override
  public void fetchAllTrades() {
    List<PlayerTradeStatus> trades = tradeStatusRepository.findAll();
    log.info("Trying to retrieve data for " + trades.size() + " players");
    trades.forEach(t -> fetchData(t.getId()));
  }

}
