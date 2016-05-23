package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@Data
@Document(collection = "monitoring_result")
@AllArgsConstructor
public class MonitoringResult {

  @Id
  private long id;

  private String name;

  private PriceHistory history;

  public String image;
}
