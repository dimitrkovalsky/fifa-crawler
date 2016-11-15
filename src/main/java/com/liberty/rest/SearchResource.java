package com.liberty.rest;

import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.SearchResponse;
import com.liberty.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
