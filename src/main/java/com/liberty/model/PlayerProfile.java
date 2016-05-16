package com.liberty.model;

import com.liberty.model.stats.OverviewStats;
import com.liberty.model.stats.Stats;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class PlayerProfile {

  private PlayerInfo info;
  private Stats stats;
  private Price price;
  private OverviewStats overviewStats;
}
