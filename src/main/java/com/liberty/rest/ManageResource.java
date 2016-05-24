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

import lombok.Data;
import lombok.NoArgsConstructor;
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
  public StringResponse fetch(@RequestBody String toFetch) {
    String trackId = "";
    try {
      switch (toFetch.toLowerCase()) {
        case "tots":
          trackId = crawlerService.fetchTots();
          break;
        case "tows":
          trackId = crawlerService.fetchTows();
          break;
        case "sources":
          trackId = crawlerService.fetchSources();
        default:
          log.error("Can not fetch : " + toFetch.toLowerCase());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return new StringResponse(trackId);
  }

  @RequestMapping(path = "/track/{id}", method = RequestMethod.GET)
  public StringResponse track(@PathVariable String id) {
    return new StringResponse(crawlerService.getStatus(id));
  }

  @RequestMapping(path = "/fetch/{id}", method = RequestMethod.GET)
  public PlayerProfile fetchOne(@PathVariable Long id, @RequestParam(value = "force",
      required = false, defaultValue = "false") boolean force) {
    return crawlerService.fetchData(id, force);
  }

  @Data
  @NoArgsConstructor
  public class StringResponse {

    private String response;

    public StringResponse(String s) {
      this.response = s;
    }
  }

}
