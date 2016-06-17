package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:08
 */
@Data
@Document(collection = "player_trade_status")
@AllArgsConstructor
@NoArgsConstructor
public class PlayerTradeStatus {

  public PlayerTradeStatus(Long id, String name, Integer maxPrice) {
    this.id = id;
    this.name = name;
    this.maxPrice = maxPrice;
  }

  @Id
  private Long id;

  private int boughtAmount;

  private String name;

  private Integer maxPrice;

  private Long lastBuyPrice;

  private Integer sellStartPrice;

  private Integer minMarketPrice;

  private Integer sellBuyNowPrice;

  private boolean enabled = true;
}
