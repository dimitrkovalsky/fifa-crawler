package com.liberty.rest.request;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
@Data
public class SellRequest {

  private Long itemId;
  private Long tradeId;
  private Long playerId;
  private Integer startPrice;
  private Integer buyNow;
}
