package com.liberty.model;

import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 22.07.2016.
 */
@Data
public class BackupModel {

  private List<PlayerTradeStatus> tradeStatuses;
  private List<PlayerStatistic> statistics;
}
