package com.liberty.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.liberty.common.Views;
import com.liberty.service.impl.HistoryServiceImpl;

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

  @JsonView(Views.Internal.class)
  public String getDate() {
    return toReadableString(innerDate);
  }

  private List<PriceDistribution> prices = new ArrayList<>();

  private Map<Long, HistoryServiceImpl.HistoryPoint> history;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


