package com.liberty.rest;

import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.SearchResponse;
import com.liberty.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
@Slf4j
public class SearchResource {

  @Autowired
  private SearchService searchService;

  @RequestMapping(method = RequestMethod.POST)
  public SearchResponse get(@RequestBody MarketSearchRequest request) {
    return new SearchResponse(searchService.search(request));
  }


}
