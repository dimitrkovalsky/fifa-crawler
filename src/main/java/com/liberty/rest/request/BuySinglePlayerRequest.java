package com.liberty.rest.request;

import lombok.Data;

/**
 * User: Dimitr
 * Date: 06.11.2016
 * Time: 10:56
 */
@Data
public class BuySinglePlayerRequest {

  private Long playerId;
  private Integer maxPrice;
  private String playerName;
}
