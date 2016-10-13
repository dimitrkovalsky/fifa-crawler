package com.liberty.processors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.common.JsonHelper;
import com.liberty.common.RequestHelper;
import com.liberty.model.Club;
import com.liberty.model.FifaPlayerSuggestion;
import com.liberty.model.League;
import com.liberty.model.Nation;
import com.liberty.model.PlayerProfile;
import com.liberty.processors.pojo.FifaResponse;
import com.liberty.processors.pojo.Items;
import com.liberty.repositories.ClubRepository;
import com.liberty.repositories.LeagueRepository;
import com.liberty.repositories.NationRepository;
import com.liberty.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Slf4j
@Component
public class FifaDatabaseProcessor {

  @Getter
  private Set<Club> clubs = new HashSet<>();
  @Getter
  private Set<League> leagues = new HashSet<>();
  @Getter
  private Set<Nation> nations = new HashSet<>();

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private ClubRepository clubRepository;

  @Autowired
  private NationRepository nationRepository;

  @Autowired
  private ImageService imageService;

  private static final String URL_PATTERN = "http://www.easports" +
      ".com/fifa/ultimate-team/api/fut/item?jsonParamObject=%s";
  private static String FIFA_PLAYERS_JSON = System.getProperty("user.dir") +
      "\\src\\main\\resources\\fifa-players.json";


  public Optional<PlayerProfile> fetchInfo(long id) {
    PlayerProfile profile = new PlayerProfile();
    try {
      String json = JsonHelper.toJson(new RequestObject(id)).toString();
      String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.name());
      String url = String.format(URL_PATTERN, encodedJson);
      log.info("Trying to fetch data from : " + url);

      String response = RequestHelper.executeRequestAndGetResult(url);
      Optional<FifaResponse> parsed = JsonHelper.toEntity(response, FifaResponse.class);

      List<Items> items = parsed.get().items;
      if (items.size() > 1) {
        log.info("FOUND MORE THAN 1 PLAYER FOR : " + id);
      }

      return fetchInfo(items.get(0));

    } catch (Exception e) {
      log.error(e.getMessage());
      return Optional.empty();
    }
  }

  private Optional<PlayerProfile> fetchInfo(Items item) {

    PlayerProfile profile = new PlayerProfile();
    profile.setId(item.getId());
    profile.setAttributes(item.getAttributes());
    profile.setBaseId(item.getBaseId());
    profile.setClubId(item.getClub().getId());

    profile.setColor(item.getColor());
    profile.setCommonName(item.getCommonName());
    profile.setFirstName(item.getFirstName());
    profile.setLastName(item.getLastName());
    profile.setGK(item.isGK());
    profile.setLeagueId(item.getLeague().getId());
    profile.setName(item.getName());
    profile.setHeadshotImgUrl(item.getHeadshotImgUrl());
    profile.setNationId(item.getNation().getId());

    profile.setRating(item.getRating());
    profile.setPlayerType(item.getPlayerType());
    profile.setQuality(item.getQuality());
    profile.setSpecialType(item.isSpecialType());
    profile.setPosition(item.getPosition());

    addClubsAndLeagues(item);
    return Optional.of(profile);
  }

  private void addClubsAndLeagues(Items item) {
    try {

      if (!leagues.contains(item.getLeague())) {
        log.info("Trying to save " + item.getLeague().getName() + " league");
        leagueRepository.save(item.getLeague());
        imageService.saveLeagueImage(item.getLeague());
        leagues.add(item.getLeague());
      }

      if (!clubs.contains(item.getClub())) {
        log.info("Trying to save " + item.getClub().getName() + " club");
        clubRepository.save(item.getClub());
        imageService.saveClubImage(item.getClub());
        clubs.add(item.getClub());
      }

      if (!nations.contains(item.getNation())) {
        log.info("Trying to save " + item.getNation().getName() + " nation");
        nationRepository.save(item.getNation());
        imageService.saveNationImage(item.getNation());
        nations.add(item.getNation());
      }
    } catch (Exception e) {
      log.error("Save clubs and leagues error : " + e.getMessage());
    }
  }

  private Optional<PlayerProfile> parseGoalkeeper(Items item) {
    return Optional.empty();
  }

  public List<FifaPlayerSuggestion> readSuggestions() {
    try {
      ObjectMapper mapper = JsonHelper.getObjectMapper();
      FifaPlayers fifaPlayers = mapper.readValue(new File(FIFA_PLAYERS_JSON), FifaPlayers.class);
      return fifaPlayers.getPlayers();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return Collections.emptyList();
  }

  @Data
  public static class FifaPlayers {

    @JsonProperty("Players")
    private List<FifaPlayerSuggestion> players;

    @JsonProperty("LegendsPlayers")
    private List<Object> legendPlayers;
  }

  @Data
  @AllArgsConstructor
  private class RequestObject {

    private long id;
  }
}
