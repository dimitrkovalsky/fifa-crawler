package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 07.11.2016.
 */
@Data
@Document(collection = "robot_request")
public class RobotRequest {

  @Id
  private Long id;
  private Long playerId;
  private Long leagueId;
  private Long nationId;
  private Long clubId;
  private Integer minPrice;
  private Integer maxPrice;
  private Integer minBuyNowPrice;
  private Integer maxBuyNowPrice;
  private String position;
  private String quality;
}
