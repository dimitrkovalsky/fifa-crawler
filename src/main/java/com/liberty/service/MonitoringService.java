package com.liberty.service;

import com.liberty.model.MonitoringResult;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public interface MonitoringService {

  void monitor(long playerId);

  List<MonitoringResult> getAllResults();

  void deleteMonitor(long id);

  Iterable<MonitoringResult> getAllByIds(List<Long> ids);

  void updatePrices();
}
