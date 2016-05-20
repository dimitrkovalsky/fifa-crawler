package com.liberty.rest;

import com.liberty.model.PlayerProfile;
import com.liberty.service.PlayerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User: Dimitr Date: 19.05.2016 Time: 23:18
 */
@RestController
@RequestMapping("/players")
public class PlayerProfileResource {

  @Autowired
  private PlayerService playerService;

  @RequestMapping(method = RequestMethod.GET)
  public List<PlayerProfile> listAll() {
    return playerService.getAllPlayers();
  }

  @RequestMapping(path = "/sources", method = RequestMethod.GET)
  public List<String> listSourcesAll() {
    return playerService.getAllSources();
  }

  @RequestMapping(method = RequestMethod.POST)
  public List<PlayerProfile> getAllBySource(String source) {
    return playerService.getAllPlayers(source);
  }

}
