package com.liberty.model.market;

import com.liberty.model.PlayerTradeStatus;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: Dimitr Date: 21.06.2016 Time: 19:49
 */
@Data
@AllArgsConstructor
public class GroupedToSell {

  private Long playerId;
  private List<ItemData> items;
  private PlayerTradeStatus tradeStatus;
}