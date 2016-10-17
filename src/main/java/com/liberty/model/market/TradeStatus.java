package com.liberty.model.market;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TradeStatus {

  private Object errorState;
  private Integer credits;
  private List<AuctionInfo> auctionInfo;
  private Object duplicateItemIdList;
  private BidTokens bidTokens;
  private ArrayList<Currency> currencies;
  private Object debug;
  private String string;
  private Integer code;
  private String reason;
}
