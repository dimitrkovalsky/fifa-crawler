package com.liberty.model.market;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PlayerStatistic {

  private Long id;
  private Integer lastPrice;
  private LocalDateTime date;

  public String getDate() {
    if (date == null)
      return "Match time ago";
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    return date.format(formatter);
  }

  private List<PriceDistribution> prices = new ArrayList<>();


  @Data
  @AllArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


