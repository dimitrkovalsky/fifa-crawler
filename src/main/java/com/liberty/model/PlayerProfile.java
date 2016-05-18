package com.liberty.model;

import com.liberty.model.stats.OverviewStats;
import com.liberty.model.stats.Stats;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
@Document(collection = "player_profile")
public class PlayerProfile {

  @Id
  private Long id;
  private PlayerInfo info;
  private Stats stats;
  private Price price;
  private OverviewStats overviewStats;
  private Date updated = new Date();
}
