package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
@Data
@Document(collection = "player_monitoring")
public class PlayerMonitoring {

  @Id
  private Long id;

  private String source;

  private Price startPrice;
}
