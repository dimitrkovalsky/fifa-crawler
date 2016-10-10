package com.liberty.model.market;

import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 10.10.2016.
 */
@Data
public class Watchlist {

  private List<AuctionInfo> auctionInfo;
  private Integer credits;
  private Integer total;
}
