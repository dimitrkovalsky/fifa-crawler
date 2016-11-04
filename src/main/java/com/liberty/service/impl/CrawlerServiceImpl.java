package com.liberty.service.impl;

import com.liberty.model.FifaPlayerSuggestion;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.Squad;
import com.liberty.processors.FifaDatabaseProcessor;
import com.liberty.processors.FutheadPlayerProcessor;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.service.CrawlerService;
import com.liberty.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

  private FutheadPlayerProcessor futheadPlayerProcessor = new FutheadPlayerProcessor();

  @Override
  public void fetchData(Long playerId) {
    PlayerProfile profile = profileRepository.findOne(playerId);
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

  @Override
  public void saveOthers() {
    processor.saveOthers();
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

  @Override
  public List<PlayerProfile> findProfilesBySquad(Long squadId) {
    List<Long> ids = futheadPlayerProcessor.getPlayerIds(squadId);
    Iterable<PlayerProfile> profiles = profileRepository.findAll(ids);
    profiles.forEach(x -> System.out.println(x.getName()));
    return toList(profiles);
  }

  @Override
  public Squad fetchBaseSquadInfo(Long squadId) {
    return futheadPlayerProcessor.fetchBaseSquadInfo(squadId);
  }

  private static <T> List<T> toList(final Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false)
        .collect(Collectors.toList());
  }
}
