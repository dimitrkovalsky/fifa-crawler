package com.liberty.rest;

import com.liberty.model.PlayerFullInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.Source;
import com.liberty.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: Dimitr Date: 19.05.2016 Time: 23:18
 */
@RestController
@RequestMapping("/api/players")
public class PlayerProfileResource {

  @Autowired
  private PlayerService playerService;

  @RequestMapping(method = RequestMethod.GET)
  public List<PlayerProfile> listAll() {
    return playerService.getAllPlayers();
  }

  @RequestMapping(path = "/sources", method = RequestMethod.GET)
  public Set<String> listSourcesAll() {
    return playerService.getAllSources().stream().map(Source::getSource).collect(Collectors.toSet());
  }

  @RequestMapping(path = "source/{source}", method = RequestMethod.GET)
  public List<PlayerProfile> getAllBySource(@PathVariable String source) {
    return playerService.getAllPlayers(source);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public PlayerFullInfo get(@PathVariable Long id) {
    return playerService.geSingle(id);
  }

}
