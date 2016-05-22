package com.liberty.rest;

import com.liberty.model.PlayerProfile;
import com.liberty.service.CrawlerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 22.05.2016 Time: 10:50
 */
@RestController
@RequestMapping("/api/manage")
@Slf4j
public class ManageResource {

  @Autowired
  private CrawlerService crawlerService;

  @RequestMapping(path = "/fetch", method = RequestMethod.POST)
  public void fetch(@RequestBody String toFetch) {
    try {

      switch (toFetch.toLowerCase()) {
        case "tots":
          crawlerService.fetchTots();
          break;
        case "tows":
          crawlerService.fetchTows();
          break;
        case "sources":
          crawlerService.fetchSources();
        default:
          log.error("Can not fetch : " + toFetch.toLowerCase());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @RequestMapping(path = "/fetch/{id}", method = RequestMethod.GET)
  public PlayerProfile fetchOne(@PathVariable Long id, @RequestParam(value = "force",
      required = false, defaultValue = "false") boolean force) {
    return crawlerService.fetchData(id, force);
  }

}
