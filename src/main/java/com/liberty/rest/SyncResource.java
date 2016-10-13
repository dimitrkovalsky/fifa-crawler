package com.liberty.rest;

import com.liberty.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/sync")
@Slf4j
public class SyncResource {

  @Autowired
  private ImageService imageService;

  @RequestMapping(value = "/update", method = RequestMethod.POST)
  public void get(@RequestBody Object body) {
   log.info("On Sync : " + body);
  }


}
