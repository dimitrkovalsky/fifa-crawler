package com.liberty.service.impl;

import com.liberty.common.AuctionHouseResponse;
import com.liberty.common.BidState;
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
import java.util.Optional;
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
    List<AuctionInfo> tradePile = requestService.getTradePile();
    Map<String, List<AuctionInfo>> byState = tradePile.stream()
        .filter(a -> !a.getTradeId().equals(0L))
        .collect(Collectors.groupingBy(AuctionInfo::getTradeState));
    if (byState.containsKey(TradeState.CLOSED)) {
      logSoldItems(byState.get(TradeState.CLOSED));
      requestService.removeAllSold();
    }
//    if (byState.containsKey(TradeState.INACTIVE)) {
//      sellAll(byState.get(TradeState.INACTIVE));
//    }
    if (byState.containsKey(TradeState.EXPIRED)) {
      logRelistItems(byState.get(TradeState.EXPIRED));
      requestService.relistAll();
    }
    return tradePile.size();
  }

  private void logRelistItems(List<AuctionInfo> auctionInfos) {
    auctionInfos.forEach(a -> {
      transactionService.logRelistOperation(a.getItemData().getAssetId(), a.getItemData().getId(), a
          .getTradeId(), a.getStartingBid(), a.getBuyNowPrice());
    });
  }

  private void logSoldItems(List<AuctionInfo> auctionInfos) {
    auctionInfos.forEach(a -> {
      transactionService.logSell(a.getItemData().getAssetId(), a.getItemData().getId(), a
          .getTradeId(), a.getCurrentBid());
    });
  }

  private void sellAll(List<AuctionInfo> auctionInfos) {
    auctionInfos.forEach(x -> {
      SellRequest request = new SellRequest();
      request.setBuyNow(x.getBuyNowPrice());
      request.setStartPrice(x.getStartingBid());
      request.setItemId(x.getItemData().getId());
      request.setPlayerId(x.getTradeId());
      // TODO: check data from transfer targets . Record transactions.
      sell(request);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  public List<ItemData> getWonTransferTargets() {
    return getTransferTargets().stream().filter(info -> {
      if (info.getTradeState().equals(TradeState.CLOSED) &&
          info.getBidState().equals(BidState.HIGHEST)) {
        return true;
      }
      return false;
    }).map(x -> {
      ItemData itemData = x.getItemData();
      itemData.setFromTargets(true);
      itemData.setTradeId(x.getTradeId());
      return itemData;
    }).collect(Collectors.toList());
  }

  @Override
  public List<GroupedToSell> getUnassigned() {
    List<ItemData> unassigned = getAllUnassigned();
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

  /**
   * Returns all unassigned items. From unassigned and from transfer targets.
   */
  private List<ItemData> getAllUnassigned() {
    List<ItemData> unassigned = requestService.getUnassigned();
    unassigned.addAll(getWonTransferTargets());
    return unassigned;
  }

  @Override
  public synchronized void sell(SellRequest request) {
    boolean itemResult;
    if (request.getTradeId() == null) {
      itemResult = requestService.item(request.getItemId());
    } else {
      itemResult = requestService.item(request.getItemId(), request.getTradeId());
    }

    if (itemResult) {
      Optional<AuctionHouseResponse> auctionResult = requestService
          .auctionHouse(request.getItemId(), request.getStartPrice(), request.getBuyNow());
      if (auctionResult.isPresent()) {
        transactionService.logPlaceToMarket(request.getPlayerId(), request.getItemId(),
            auctionResult.get().getId(), request.getStartPrice(), request.getBuyNow());
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
    int unassigned = getAllUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    Integer availableCredits = requestService.getWatchlist().getCredits();
    int credits = 0;
    if (availableCredits != null) {
      credits = availableCredits;
    }
    return new BuyMessage(unassigned, canSell, credits, getPurchasesRemained());
  }

  @Override
  public Watchlist getWatchlist() {
    return requestService.getWatchlist();
  }

  @Override
  public void logBuyOrSell() {
    int unassigned = getAllUnassigned().size();
    int canSell = TRADEPILE_SIZE - getTradePileSize();
    int credits = requestService.getWatchlist().getCredits();
    logController.logBuy(unassigned, canSell, credits, getPurchasesRemained());
  }


  protected abstract Integer getPurchasesRemained();
}
