package com.liberty.service.impl;

import com.liberty.common.TradeState;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.TradeService;
import com.liberty.websockets.BuyMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
public abstract class ASellService extends ATradeService implements TradeService {

  public static final int TRADEPILE_SIZE = 70;

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
  public List<GroupedToSell> getUnassigned() {
    List<ItemData> unassigned = fifaRequests.getUnassigned();
    Map<Long, List<ItemData>> map =
        unassigned.stream().collect(Collectors.groupingBy(ItemData::getAssetId));
    List<GroupedToSell> result = new ArrayList<>();
    map.forEach((k, v) -> {
      result.add(new GroupedToSell(k, v, tradeRepository.findOne(k)));
    });
    return result;
    // sell(unassigned.get(0), 1700, 1900);
  }

  @Override
  public synchronized void sell(SellRequest request) {
    if (fifaRequests.item(request.getItemId())) {
      boolean success = fifaRequests
          .auctionHouse(request.getItemId(), request.getStartPrice(), request.getBuyNow());
      if (success) {
        logBuyOrSell();

        PlayerTradeStatus player = tradeRepository.findOne(request.getPlayerId());
        logController.info("Success placed to market : " + player.getName() + " startPrice: " +
            request.getStartPrice() + " buyNow: " + request.getBuyNow());
        player.setSellStartPrice(request.getStartPrice());
        player.setSellBuyNowPrice(request.getBuyNow());
        tradeRepository.save(player);
      }
    }
  }

  @Override
  public BuyMessage getTradepileInfo() {
    int unassigned = fifaRequests.getUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    return new BuyMessage(unassigned, canSell, getPurchasesRemained());
  }

  protected void logBuyOrSell() {
    int unassigned = fifaRequests.getUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    logController.logBuy(unassigned, canSell, getPurchasesRemained());
  }


  protected abstract Integer getPurchasesRemained();
}
