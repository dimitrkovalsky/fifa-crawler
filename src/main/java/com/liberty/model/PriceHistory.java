package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
@Data
@Document(collection = "price_history")
public class PriceHistory {

  @Id
  private long id;
  private Date created = new Date();

  private Price currentPrice;
  private Price lastPrice;
  private List<PriceRecord> history = new ArrayList<>();

  public void addPrice(Price oldPrice) {
    history.add(new PriceRecord(oldPrice, new Date()));
  }

  @Data
  @AllArgsConstructor
  public static class PriceRecord {

    private Price price;
    private Date recoded;
  }
}
