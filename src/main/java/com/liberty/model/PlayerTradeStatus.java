package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

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

  private LocalDateTime innerDate;

  private String lastUpdate;

  private boolean enabled = true;

  public String getLastDate() {
    if (innerDate == null) {
      return "Match time ago";
    }

    LocalDateTime now = LocalDateTime.now();
    Duration duration = Duration.between(innerDate, now);
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

  public void updateDate() {
    innerDate = LocalDateTime.now();
  }
}
