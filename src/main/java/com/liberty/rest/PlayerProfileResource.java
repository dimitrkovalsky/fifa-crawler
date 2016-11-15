package com.liberty.rest;

import com.liberty.model.PlayerProfile;
import com.liberty.rest.request.SearchRequest;
import com.liberty.service.PlayerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@RestController
@RequestMapping("/api/profiles")
public class PlayerProfileResource {

    @Autowired
    private PlayerProfileService playerProfileService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public List<PlayerProfile> getAll(SearchRequest request) {
        String phrase = request.getPhrase() == null ? "" : request.getPhrase();
        return playerProfileService.searchByPhrase(phrase);
    }


}
