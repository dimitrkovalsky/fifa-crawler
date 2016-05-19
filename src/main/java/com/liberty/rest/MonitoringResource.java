package com.liberty.rest;

import com.liberty.model.MonitoringResult;
import com.liberty.service.MonitoringService;

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
@RequestMapping("/monitoring")
public class MonitoringResource {

  @Autowired
  private MonitoringService monitoringService;

  @RequestMapping(method = RequestMethod.GET)
  public List<MonitoringResult> listAll() {
    return monitoringService.getAllResults();
  }
}
