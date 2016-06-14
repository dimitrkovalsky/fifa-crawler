package com.liberty.model.market;

import java.util.List;

import lombok.Data;

@Data
public class PlayerStatistic {

  private Long id;
  private Integer lastPrice;
  private List<PriceDistribution> prices;


  @Data
  public static class PriceDistribution {

    private Integer amount;
    private Integer price;
  }
}


