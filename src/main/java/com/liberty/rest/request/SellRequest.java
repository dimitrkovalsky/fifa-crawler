package com.liberty.rest.request;

import com.liberty.model.market.ItemData;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
@Data
public class SellRequest {

  private ItemData itemData;
  private Integer startPrice;
  private Integer buyNow;
}
