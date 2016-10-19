package com.liberty.rest;

import com.liberty.model.Club;
import com.liberty.model.League;
import com.liberty.model.Nation;
import com.liberty.repositories.ClubRepository;
import com.liberty.repositories.LeagueRepository;
import com.liberty.repositories.NationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/info")
@Slf4j
public class InfoResource {

  @Autowired
  private LeagueRepository leagueRepository;

  @Autowired
  private ClubRepository clubRepository;

  @Autowired
  private NationRepository nationRepository;

  @RequestMapping(value = "/leagues", method = RequestMethod.GET)
  public Map<Long, League> getLeagues() {
    Map<Long, League> map = new HashMap<>();
    leagueRepository.findAll().forEach(x -> map.put(x.getId(), x));
    return map;
  }

  @RequestMapping(value = "/nations", method = RequestMethod.GET)
  public Map<Long, Nation> getNations() {
    Map<Long, Nation> map = new HashMap<>();
    nationRepository.findAll().forEach(x -> map.put(x.getId(), x));
    return map;
  }

  @RequestMapping(value = "/clubs", method = RequestMethod.GET)
  public Map<Long, Club> getClubs() {
    Map<Long, Club> map = new HashMap<>();
    clubRepository.findAll().forEach(x -> map.put(x.getId(), x));
    return map;
  }


}
