package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:08
 */
@Data
@Document(collection = "player_trade_status")
@AllArgsConstructor
public class PlayerTradeStatus {

  @Id
  private Long id;

  private int boughtAmount;

  private String name;

  private int maxPrice;
}
