package com.liberty.model.market;

import java.time.Duration;
import java.time.LocalDateTime;
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
    if (date == null) {
      return "Match time ago";
    }

    LocalDateTime now = LocalDateTime.now();
    Duration duration = Duration.between(date, now);
    long days = duration.toDays();
    long hours = duration.toHours();
    long minutes = duration.toMinutes();
    if (minutes < 60) {
      return minutes + " minutes ago";
    } else if (hours < 24) {
      return String.format("%s hours %s minutes ago", (int) minutes / 60, minutes - hours * 60);
    } else if (days == 0) {
      return hours + " hours ago";
    }

    return String.format("%s days %s hours ago", (int) hours / 24, hours - days * 24);
  }

  private List<PriceDistribution> prices = new ArrayList<>();


  @Data
  @AllArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


