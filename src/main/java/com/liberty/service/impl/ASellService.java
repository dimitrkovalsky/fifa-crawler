package com.liberty.service.impl;

import com.liberty.common.TradeState;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.PlayerProfileService;
import com.liberty.service.TradeService;
import com.liberty.websockets.BuyMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
@Component
public abstract class ASellService extends ATradeService implements TradeService {

  public static final int TRADEPILE_SIZE = 70;

  @Autowired
  protected PlayerProfileService playerProfileService;

  @Override
  public int getTradePileSize() {
    List<AuctionInfo> tradePile = fifaRequests.getTradePile();
    Map<String, List<AuctionInfo>> byState = tradePile.stream()
        .filter(a -> !a.getTradeId().equals(0L))
        .collect(Collectors.groupingBy(AuctionInfo::getTradeState));
    if (byState.containsKey(TradeState.CLOSED)) {
      fifaRequests.removeAllSold();
    }
//    if (byState.containsKey(TradeState.INACTIVE)) {
//      sellAll(byState.get(TradeState.INACTIVE));
//    }
    if (byState.containsKey(TradeState.EXPIRED)) {
      fifaRequests.relistAll();
    }
    tradePile = fifaRequests.getTradePile();
    return tradePile.size();
  }

  private void sellAll(List<AuctionInfo> auctionInfos) {
    auctionInfos.forEach(x -> {
      SellRequest request = new SellRequest();
      request.setBuyNow(x.getBuyNowPrice());
      request.setStartPrice(x.getStartingBid());
      request.setItemId(x.getItemData().getId());
      request.setPlayerId(x.getTradeId());
      sell(request);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
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

    List<Long> ids = result.stream().map(GroupedToSell::getPlayerId).collect(Collectors.toList());
    List<PlayerProfile> profiles = playerProfileService.getAll(ids);
    Map<Long, PlayerProfile> profileMap = new HashMap<>();

    profiles.forEach(p -> profileMap.put(p.getId(), p));
    result.forEach(r -> r.setProfile(profileMap.get(r.getPlayerId())));
    return result;
  }

  @Override
  public synchronized void sell(SellRequest request) {
    if (fifaRequests.item(request.getItemId())) {
      boolean success = fifaRequests
          .auctionHouse(request.getItemId(), request.getStartPrice(), request.getBuyNow());
      if (success) {
        logBuyOrSell();
        try {

          PlayerTradeStatus player = tradeRepository.findOne(request.getPlayerId());
          logController.info("Success placed to market : " + player.getName() + " startPrice: " +
              request.getStartPrice() + " buyNow: " + request.getBuyNow());
          player.setSellStartPrice(request.getStartPrice());
          player.setSellBuyNowPrice(request.getBuyNow());
          tradeRepository.save(player);
        } catch (Exception e) {
          System.out.println("Can not find player : " + request.getPlayerId());
        }
      }
    }
  }

  @Override
  public BuyMessage getTradepileInfo() {
    int unassigned = fifaRequests.getUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    int credits = fifaRequests.getWatchlist().getCredits();
    return new BuyMessage(unassigned, canSell, credits, getPurchasesRemained());
  }

  @Override
  public Watchlist getWatchlist() {
      return fifaRequests.getWatchlist();
  }

  @Override
  public void logBuyOrSell() {
    int unassigned = fifaRequests.getUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    int credits = fifaRequests.getWatchlist().getCredits();
    logController.logBuy(unassigned, canSell, credits, getPurchasesRemained());
  }


  protected abstract Integer getPurchasesRemained();
}
