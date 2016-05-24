package com.liberty.rest;

import com.liberty.model.MonitoringResult;
import com.liberty.rest.request.LongList;
import com.liberty.service.MonitoringService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringResource {

  @Autowired
  private MonitoringService monitoringService;

  @RequestMapping(method = RequestMethod.GET)
  public List<MonitoringResult> listAll() {
    return monitoringService.getAllMonitored();
  }

  @RequestMapping(method = RequestMethod.POST)
  public void add(@RequestBody Long id) {
    monitoringService.monitor(id);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  public void delete(@PathVariable Long id) {
    monitoringService.deleteMonitor(id);
  }

  @RequestMapping(path = "/all", method = RequestMethod.POST)
  public Iterable<MonitoringResult> getAll(LongList ids) {
    return monitoringService.getAllByIds(ids.getIds());
  }
}