package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Data
@AllArgsConstructor
public class SquadPlayer {

  private Long playerId;
  private PlayerProfile profile;
  private PlayerStatistic.PriceDistribution minPrice;
  private Long median;
  private boolean inClub;
  private PlayerTradeStatus tradeStatus;
  private String lastUpdate;

}
