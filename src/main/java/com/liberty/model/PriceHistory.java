package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
@Data
@Document(collection = "price_history")
public class PriceHistory {

  @Id
  private int id;
  private Date created;

  private Price currentPrice;
  private Price lastPrice;
  private List<Price> history = new ArrayList<>();
}
