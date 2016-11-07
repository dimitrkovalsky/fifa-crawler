package com.liberty.service;

import com.liberty.model.MarketConfig;

import java.util.Map;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.05.2016.
 */
public interface ConfigService {

  Map<String, Integer> getTagDistribution();

  Set<String> getActiveTags();

  void deactivateTag(String tag);

  MarketConfig getMarketConfig();

  void activateTag(String tag);

  void updateActivePlayersPrices();

  void cleanHistory();

}
