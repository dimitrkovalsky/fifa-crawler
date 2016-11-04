package com.liberty.model;

import com.liberty.common.PriceHelper;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.liberty.common.DateHelper.toReadableString;

@Data
@Document(collection = "player_statistic")
public class PlayerStatistic {

  private Long id;
  private Integer lastPrice;

  private LocalDateTime innerDate;

  public String getDate() {
    return toReadableString(innerDate);
  }

  private List<PriceDistribution> prices = new ArrayList<>();

  private Map<Long, PriceHelper.HistoryPoint> history;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


