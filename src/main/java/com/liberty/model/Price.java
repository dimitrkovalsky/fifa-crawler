package com.liberty.model;

import com.liberty.common.Platform;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
@EqualsAndHashCode
public class Price {

  private SpecificPrice xbox;
  private SpecificPrice ps;
  private SpecificPrice pc;

  public boolean isEmpty() {
    return xbox == null && ps == null && pc == null;
  }


  @Data
  public static class SpecificPrice {

    private Platform platform;
    private String lastUpdate;
    private int price;
    private int minPrice;
    private int maxPrice;

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SpecificPrice that = (SpecificPrice) o;

      if (price != that.price) return false;
      if (minPrice != that.minPrice) return false;
      if (maxPrice != that.maxPrice) return false;
      return platform == that.platform;

    }

    @Override
    public int hashCode() {
      int result = platform.hashCode();
      result = 31 * result + price;
      result = 31 * result + minPrice;
      result = 31 * result + maxPrice;
      return result;
    }
  }
}
