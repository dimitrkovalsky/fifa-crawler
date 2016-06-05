package com.liberty.model.market;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * User: Dimitr Date: 04.06.2016 Time: 16:55
 */
@Data
public class SellItem {

  private List<ItemData> itemData = new ArrayList<>();

  public SellItem(long id) {
    ItemData data = new ItemData();
    data.setId(id);
    itemData.add(data);
  }

  @Data
  public static class ItemData {

    Long id;
    String pile = "trade";
    Boolean success;
  }
}
