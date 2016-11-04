package com.liberty.rest;

import com.liberty.model.FullSquad;
import com.liberty.service.SquadBuilderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/squad")
@Slf4j
public class SquadResource {

  @Autowired
  private SquadBuilderService squadService;


  @RequestMapping(path = "/{squadId}", method = RequestMethod.GET)
  public FullSquad getSquad(@PathVariable Long squadId) {
    return squadService.fetchPricesForSquad(squadId);
  }

}
