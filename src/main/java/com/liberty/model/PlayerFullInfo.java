package com.liberty.model;

import lombok.Data;

/**
 * User: Dimitr Date: 26.05.2016 Time: 8:40
 */
@Data
public class PlayerFullInfo {

  private PlayerProfile profile;
  private PriceHistory history;
}
