package com.liberty.service.impl;

import com.liberty.common.*;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.GroupedToSell;
import com.liberty.model.market.ItemData;
import com.liberty.model.market.Watchlist;
import com.liberty.rest.request.SellRequest;
import com.liberty.service.NoActivityService;
import com.liberty.service.PlayerProfileService;
import com.liberty.service.TradeService;
import com.liberty.service.adapters.MinerAdapter;
import com.liberty.service.strategy.SellStrategy;
import com.liberty.websockets.BuyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.06.2016.
 */
@Component
@Slf4j
public abstract class ASellService extends ATradeService implements TradeService {

    public static final int TRADEPILE_SIZE = 70;

    @Autowired
    protected PlayerProfileService playerProfileService;

    @Autowired
    protected MinerAdapter miner;

    @Autowired
    private SellStrategy sellStrategy;

    @Autowired
    protected NoActivityService noActivityService;

    protected boolean autoSellRelistMinerEnabled;
    private volatile boolean inRelist = false;
    private boolean sellLowerPrice = false;

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
            relist(byState.get(TradeState.EXPIRED));

        }
        return tradePile.size();
    }

    private void relist(List<AuctionInfo> auctionInfos) {
        if (inRelist)
            return;
        inRelist = true;
        if (autoSellRelistMinerEnabled && miner.isAlive()) {
            minerRelist(auctionInfos);
        }
        requestService.relistAll();
        logRelistItems(auctionInfos);
        inRelist = false;
    }

    private void minerRelist(List<AuctionInfo> auctionInfos) {
        int count = 0;
        Set<Long> toUpdate = new HashSet<>();
        for (AuctionInfo auctionInfo : auctionInfos) {
            ItemData itemData = auctionInfo.getItemData();
            PlayerTradeStatus status = tradeRepository.findOne(itemData.getAssetId());
            if (status == null)
                continue;
            if (itemData.getRareflag() >= 2) {
                continue;
            }
            if (!sellStrategy.isPriceDistributionActual(status.getId())) {
                toUpdate.add(status.getId());
            } else if (sellLowerPrice || sellStrategy.shouldSell(itemData, status)) {
                SellRequest request = sellStrategy.defineBid(itemData, status);
                int from = PriceHelper.calculateProfit(itemData.getLastSalePrice(), request.getStartPrice());
                int to = PriceHelper.calculateProfit(itemData.getLastSalePrice(), request.getBuyNow());
                logController.info("Trying to relist item " + status.getName() + " for start price "
                        + request.getStartPrice() + " . Profit from " + from + " to " + to);
                relistItem(request);
                DelayHelper.wait(5000, 200);
                count++;
            } else {
                log.info("[Miner] decided do not sell player: " +
                        playerProfileService.findOne(auctionInfo.getItemData().getAssetId()).getName());
            }
        }
        if (!toUpdate.isEmpty()) {
            noActivityService.shouldUpdate(toUpdate);
            log.info("ASellService asked to update prices for : " + toUpdate.size());
        }
        log.info("Miner relisted " + count + " / " + auctionInfos.size() + " players...");
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
    @Override
    public List<ItemData> getAllUnassigned() {
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
    public synchronized void sellAuto(SellRequest request) {
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
                try {
                    PlayerTradeStatus player = tradeRepository.findOne(request.getPlayerId());
                    logController.info("Success placed to market : " + player.getName() + " startPrice: " +
                            request.getStartPrice() + " buyNow: " + request.getBuyNow());
                } catch (Exception e) {
                    System.out.println("Can not find player : " + request.getPlayerId());
                }
            }
        }
    }

    public void relistItem(SellRequest request) {
        Optional<AuctionHouseResponse> auctionResult = requestService
                .auctionHouse(request.getItemId(), request.getStartPrice(), request.getBuyNow());
        if (auctionResult.isPresent()) {
            try {
                PlayerTradeStatus player = tradeRepository.findOne(request.getPlayerId());
                logController.info("Success relisted item : " + player.getName() + " startPrice: " +
                        request.getStartPrice() + " buyNow: " + request.getBuyNow());
            } catch (Exception e) {
                System.out.println("Can not find player : " + request.getPlayerId());
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
