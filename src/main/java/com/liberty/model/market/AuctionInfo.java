package com.liberty.model.market;

import lombok.Data;

@Data
public class AuctionInfo {

  private Long tradeId;
  private ItemData itemData;
  private String tradeState;
  private Integer buyNowPrice;
  private int currentBid;
  private int offers;
  private Object watched;
  private String bidState;
  private int startingBid;
  private int confidenceValue;
  private int expires;
  private String sellerName;
  private int sellerEstablished;
  private int sellerId;
  private boolean tradeOwner;
  private String tradeIdStr;
}
