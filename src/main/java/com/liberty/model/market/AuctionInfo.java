package com.liberty.model.market;

import lombok.Data;

@Data
public class AuctionInfo {

  private Long tradeId;
  private ItemData itemData;
  private String tradeState;
  private Integer buyNowPrice;
  private Integer currentBid;
  private Integer offers;
  private Object watched;
  private String bidState;
  private Integer startingBid;
  private Integer confidenceValue;
  private Integer expires;
  private String sellerName;
  private Integer sellerEstablished;
  private Integer sellerId;
  private Boolean tradeOwner;
  private String tradeIdStr;
  private Long coinsProcessed;
  private Long duration;


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AuctionInfo info = (AuctionInfo) o;

    if (sellerId != info.sellerId) return false;
    if (tradeOwner != info.tradeOwner) return false;
    if (!tradeId.equals(info.tradeId)) return false;
    if (!buyNowPrice.equals(info.buyNowPrice)) return false;
    if (sellerName != null ? !sellerName.equals(info.sellerName) : info.sellerName != null)
      return false;
    return tradeIdStr != null ? tradeIdStr.equals(info.tradeIdStr) : info.tradeIdStr == null;

  }

  @Override
  public int hashCode() {
    int result = tradeId.hashCode();
    result = 31 * result + buyNowPrice.hashCode();
    result = 31 * result + (sellerName != null ? sellerName.hashCode() : 0);
    result = 31 * result + sellerId;
    result = 31 * result + (tradeOwner ? 1 : 0);
    result = 31 * result + (tradeIdStr != null ? tradeIdStr.hashCode() : 0);
    return result;
  }
}
