package com.liberty.model.market;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 10.10.2016.
 */
@Data
public class Watchlist {

  private List<AuctionInfo> auctionInfo = new ArrayList<>();
  private List<Object> duplicateItemIdList;
  private Integer credits;
  private Integer total;
}
