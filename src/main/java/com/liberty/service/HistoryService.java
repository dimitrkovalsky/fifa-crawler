package com.liberty.service;

import com.liberty.model.PlayerProfile;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public interface HistoryService {

  void recordHistory(PlayerProfile profile);

}
