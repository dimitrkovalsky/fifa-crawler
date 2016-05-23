package com.liberty.rest;

import com.liberty.model.Statistic;
import com.liberty.service.StatisticService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
@RestController
@RequestMapping("/api/stats")
@Slf4j
public class StatisticResource {

  @Autowired
  private StatisticService statisticService;

  @RequestMapping(method = RequestMethod.GET)
  public Statistic get() {
    return statisticService.getGeneralStatistic();
  }
}
