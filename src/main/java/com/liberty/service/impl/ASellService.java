package com.liberty.service.impl;

import com.liberty.common.TradeState;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.TradeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
public abstract class ASellService extends ATradeService implements TradeService {

  @Override
  public int getTradePileSize() {
    List<AuctionInfo> tradePile = fifaRequests.getTradePile();
    Map<String, List<AuctionInfo>> byState = tradePile.stream()
        .collect(Collectors.groupingBy(AuctionInfo::getTradeState));
    if (byState.containsKey(TradeState.CLOSED)) {
      fifaRequests.removeAllSold();
    }
    if (byState.containsKey(TradeState.EXPIRED)) {
      fifaRequests.relistAll();
    }
    tradePile = fifaRequests.getTradePile();
    return tradePile.size();
  }

  @Override
  public List<ItemData> getUnassigned() {
    List<ItemData> unassigned = fifaRequests.getUnassigned();
    // sell(unassigned.get(0), 1700, 1900);
    return unassigned;
  }

  @Override
  public void sell(SellRequest request) {
    if (fifaRequests.item(request.getItemData())) {
      fifaRequests.auctionHouse(request.getItemData(), request.getStartPrice(), request.getBuyNow());
    }
  }
}
