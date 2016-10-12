package com.liberty.rest;

import com.liberty.model.PlayerProfile;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.rest.request.SearchRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@RestController
@RequestMapping("/api/profiles")
public class PlayerProfileResource {

  @Autowired
  private PlayerProfileRepository profileRepository;

  @RequestMapping(path = "/search", method = RequestMethod.GET)
  public List<PlayerProfile> getAll(SearchRequest request) {
    String phrase = request.getPhrase() == null ? "" : request.getPhrase();
    List<PlayerProfile> players = profileRepository.findByName(phrase);
    if (players == null)
      return Collections.emptyList();
    return players.stream()
        .sorted((o1, o2) -> -o1.getRating().compareTo(o2.getRating()))
        .collect(Collectors.toList());
  }


}
