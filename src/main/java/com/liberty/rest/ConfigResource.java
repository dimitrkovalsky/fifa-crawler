package com.liberty.rest;

import com.liberty.rest.request.NewTagRequest;
import com.liberty.rest.request.StringRequest;
import com.liberty.rest.response.ConfigResponse;
import com.liberty.service.ConfigService;
import com.liberty.service.TagService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/config")
@Slf4j
public class ConfigResource {

  @Autowired
  private ConfigService configService;

  @Autowired
  private TagService tagService;

  @RequestMapping(method = RequestMethod.GET)
  public ConfigResponse get() {
    Map<String, Integer> tagDistribution = configService.getTagDistribution();
    Set<String> activeTags = configService.getActiveTags();
    return new ConfigResponse(tagDistribution, activeTags);
  }

  @RequestMapping(path = "/activate", method = RequestMethod.POST)
  public void activateTag(@RequestBody StringRequest request) {
    configService.activateTag(request.getString());
  }

  @RequestMapping(path = "/deactivate", method = RequestMethod.POST)
  public void deactivateTag(@RequestBody StringRequest request) {
    configService.deactivateTag(request.getString());
  }

  @RequestMapping(path = "/update", method = RequestMethod.POST)
  public void updateActive() {
    configService.updateActivePlayersPrices();
  }

  @RequestMapping(path = "/tag/add", method = RequestMethod.POST)
  public void addTag(@RequestBody NewTagRequest request) {
    tagService.addNewTag(request.getTag());
  }

}
