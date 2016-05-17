package com.liberty.processors;

import com.liberty.common.RequestHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.Price;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.05.2016.
 */
public class PriceProcessor {

  public Price process(PlayerInfo info) {
    Price price = new Price();

    return price;
  }

  public static void main(String[] args) {
    System.out.println(RequestHelper.executeWithJs("http://www.futhead.com/16/players/25699/harry-kane/"));
  }
}
