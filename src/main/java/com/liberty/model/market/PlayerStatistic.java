package com.liberty.model.market;

import com.fasterxml.jackson.annotation.JsonView;
import com.liberty.common.Views;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.liberty.common.DateHelper.toReadableString;

@Data
public class PlayerStatistic {

  private Long id;
  private Integer lastPrice;

  private LocalDateTime innerDate;

  @JsonView(Views.Internal.class)
  public String getDate() {
    return toReadableString(innerDate);
  }

  private List<PriceDistribution> prices = new ArrayList<>();


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PriceDistribution {

    private Integer price;
    private Integer amount;
  }
}


