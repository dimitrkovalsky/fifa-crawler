package com.liberty.rest.request;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.10.2016.
 */
@Data
public class MarketSearchRequest {
  private Long playerId;
  private Long leagueId;
  private Long nationId;
  private Long clubId;
  private Integer minPrice;
  private Integer maxPrice;
  private Integer minBuyNowPrice;
  private Integer maxBuyNowPrice;
  private String quality;

}
