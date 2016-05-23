package com.liberty.service;

import com.liberty.model.MonitoringResult;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public interface MonitoringService {

  void monitor(long playerId);

  List<MonitoringResult> getAllResults();

  List<MonitoringResult> getAllMonitored();

  void deleteMonitor(long id);

  Iterable<MonitoringResult> getAllByIds(List<Long> ids);

  void updatePrices(Consumer<MonitoringResult> onUpdate);
}
