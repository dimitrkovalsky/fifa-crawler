package com.liberty.service;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.service.impl.HistoryServiceImpl;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public interface HistoryService {

  void recordHistory(PlayerProfile profile);

  HistoryServiceImpl.RecordResult recordHistory(long playerId, Price price);
}
