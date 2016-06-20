package com.liberty.model.market;

import java.util.List;

import lombok.Data;

/**
 * User: Dimitr Date: 20.06.2016 Time: 21:22
 */
@Data
public class Items {

  private List<ItemData> itemData;
  private List<DuplicateItem> duplicateItemIdList;

  @Data
  public static class DuplicateItem {
      private Long itemId;
      private Long duplicateItemId;
  }
}
