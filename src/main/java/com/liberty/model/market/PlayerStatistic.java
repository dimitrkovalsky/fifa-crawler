package com.liberty.model.market;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayerStatistic {

  private Long id;
  private Integer lastPrice;
  private List<PriceDistribution> prices;


  @Data
  @AllArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


