package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private PriceRecord firstPrice;

  private PriceRecord currentPrice;
  private PriceRecord lastPrice;
  private List<PriceRecord> history = new ArrayList<>();

  public void addPrice(PriceRecord oldPrice) {
    history.add(oldPrice);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PriceRecord {

    private Price price;
    private Date recoded;

    public PriceRecord(Price price) {
      this.price = price;
      this.recoded = new Date();
    }

  }
}
