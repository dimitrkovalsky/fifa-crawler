package com.liberty.model;

import com.liberty.common.Platform;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class Price {

  private SpecificPrice xbox;
  private SpecificPrice ps;
  private SpecificPrice pc;


  @Data
  public static class SpecificPrice {

    Platform platform;
    String lastUpdate;
    float price;
    float minPrice;
    float maxPrice;
  }
}
