package com.liberty.model.market;

import java.util.List;

import lombok.Data;

/**
 * User: Dimitr Date: 04.06.2016 Time: 16:42
 */
@Data
public class BidResponse {

  private Object errorState;
  private Long credits;
  private Long code;
  private List<AuctionInfo> auctionInfo;
  private List<Object> duplicateItemIdList;
  private Object bidTokens;
  private List<Currency> currencies;
  private Object debug;

}
