package com.liberty.service.impl;

import com.liberty.common.BoundHelper;
import com.liberty.common.UrlResolver;
import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.TradeInfo;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.rest.request.AutobidRequest;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.StatisticService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.liberty.common.BoundHelper.defineLowBound;
import static com.liberty.common.BoundHelper.getHigherBound;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:11
 */
@Service
public class TradeServiceImpl extends ASellService implements TradeService {

  public static final int DEFAULT_LOW_BOUND = 4000;
  public static final int STATISTIC_PLAYER_COLLECTION_AMOUNT = 15;
  public static final int ITERATION_LIMIT = 35;

  private boolean autoBuyEnabled = true;

  // TODO: add rest to change active tag
  private Optional<String> activeTag = Optional.empty();

  @Autowired
  private PlayerStatisticRepository statisticRepository;

  @Autowired
  private StatisticService statisticService;

  @Autowired
  private PlayerProfileRepository profileRepository;


  @Override
  public void removeAllPlayers() {
    tradeRepository.deleteAll();
  }

  @Override
  public void updatePrices() {
    Map<Long, Integer> pricesMap = getMinPricesMap();
    tradeRepository.findAll().stream()
        .map(p -> {
          p.setEnabled(false);
          Integer price = pricesMap.get(p.getId());
          if (price != null && price != 0) {
            p.setEnabled(true);
            if (price <= 1000) {
              p.setMaxPrice(price - 200);
            } else if (price <= 2000) {
              p.setMaxPrice(price - 300);
            } else if (price <= 3000) {
              p.setMaxPrice(price - 400);
            } else if (price <= 4000) {
              p.setMaxPrice(price - 500);
            } else if (price <= 5000) {
              p.setMaxPrice(price - 700);
            } else {
              p.setEnabled(false);
            }
          }
          p.updateDate();
          return p;
        }).forEach(tradeRepository::save);
  }

  @Override
  public void checkMarket() {
    if (!autoBuyEnabled || failed) {
      logController.info("Auto Buy Disabled...");
      return;
    }
    if (purchases >= maxPurchaseAmount) {
      logController.info(
          "MAX purchase amount is : " + maxPurchaseAmount + " currently bought " + purchases);
      return;
    }
    List<PlayerTradeStatus> players = tradeRepository.findAll().stream()
        .filter(filterPlayersToAutoBuy())
        .collect(Collectors.toList());
    Collections.shuffle(players, new Random(System.currentTimeMillis()));
    logController.info("Monitor : " + players.size() + " players");
    if (isEmpty(players)) {
      logController.info("Nothing to buy. Player trade is empty");
      return;
    }
    for (PlayerTradeStatus p : players) {
      logController.info("Trying to check " + p.getName() + " max price => " + p.getMaxPrice());
      if (!autoBuyEnabled) {
        return;
      }
      boolean success = checkMarket(p);
      if (!success) {
        failed = false;
        break;
      }
      logController.info("Total purchases : " + purchases);
      if (purchases >= maxPurchaseAmount) {
        logController.info("Limit of purchases : " + purchases);
        failed = true;
        onFailed();
        return;
      }
      sleep();
    }
  }

  private Predicate<PlayerTradeStatus> filterPlayersToAutoBuy() {
    return p -> {
      if (activeTag.isPresent()) {
        return p.isEnabled() && p.getTags().contains(activeTag.get());
      }
      return p.isEnabled();
    };
  }

  private boolean checkMarket(PlayerTradeStatus playerTradeStatus) {
    try {
      sleep();
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerTradeStatus.getId(),
          playerTradeStatus.getMaxPrice(), 0);
      if (!maybe.isPresent()) {
        return false;
      }
      TradeStatus tradeStatus = maybe.get();
      int found = tradeStatus.getAuctionInfo().size();
      logController
          .info("Found " + found + " players for " + playerTradeStatus.getName() + " maxPrice : "
              + playerTradeStatus.getMaxPrice());
      if (found <= 0) {
        return true;
      }
      buyPlayers(tradeStatus, playerTradeStatus);
    } catch (Exception e) {
      logController.error(e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public void addToAutoBuy(String name, long id, int maxPrice) {
    PlayerTradeStatus status = new PlayerTradeStatus(id, name, maxPrice);
    status.updateDate();
    tradeRepository.save(status);
  }

  @Override
  public void findMinPriceAll() {
    List<PlayerTradeStatus> all = tradeRepository.findAll();
    Collections.sort(all, Comparator.comparingLong(PlayerTradeStatus::getMaxPrice));

    final int[] counter = {0};
    all.forEach(p -> {
      findMinPrice(p.getId());
      counter[0]++;
      logController.info("Updated market price for " + counter[0] + " / " + all.size());
      sleep(7000);
    });
  }

  @Override
  public PlayerStatistic findMinPrice(long playerId) {
    PlayerStatistic playerStatistic = statisticRepository.findOne(playerId);
    PlayerTradeStatus tradeStatus = tradeRepository.findOne(playerId);
    PlayerProfile profile = playerProfileService.findOne(playerId);
    if (playerStatistic == null) {
      playerStatistic = new PlayerStatistic();
      playerStatistic.setId(playerId);
    }
    if (tradeStatus == null) {
      tradeStatus = createNewTrade(profile);
    }
    Integer lowBound = defineLowBound(playerStatistic, tradeStatus);

    int iteration = 0;
    Set<AuctionInfo> toStatistic = new HashSet<>();

    while (toStatistic.size() < STATISTIC_PLAYER_COLLECTION_AMOUNT && !isMaxBound(profile)) {
      iteration++;
      logController.info("Trying to find " + tradeStatus.getName() + " less than " + lowBound);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      List<AuctionInfo> players = findPlayers(playerId, lowBound, 0);
      if (players.size() == 0) {
        lowBound = getHigherBound(0, lowBound);
      } else if (players.size() >= 12) {
        players.addAll(findNextPagesPlayers(playerId, lowBound));
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      } else {
        toStatistic.addAll(players);
        lowBound = getHigherBound(0, lowBound);
      }
      logController.info("Found " + toStatistic.size() + " players");

      if (iteration >= ITERATION_LIMIT) {
        logController.info("Exceeded iteration limit");
        break;
      }
    }

    statisticService.collectStatistic(playerId, lowBound, toStatistic);

    logController.info("Found " + toStatistic.size() + " players in " + iteration + " iterations");
    return getMinPrice(playerId);
  }

  private boolean isMaxBound(PlayerProfile profile) {
    return false;
  }

  private PlayerTradeStatus createNewTrade(PlayerProfile profile) {
    PlayerTradeStatus tradeStatus = new PlayerTradeStatus(profile.getId(), profile.getName(),
        BoundHelper.defineMaxBuyNow(profile));
    tradeStatus.setEnabled(false);
    tradeRepository.save(tradeStatus);
    tradeStatus.updateDate();
    return tradeStatus;
  }

  private List<AuctionInfo> findNextPagesPlayers(long playerId, Integer lowBound) {
    boolean completed = false;
    int page = 1;
    List<AuctionInfo> players = new ArrayList<>();
    while (!completed) {
      List<AuctionInfo> found = findPlayers(playerId, lowBound, page * 12);
      if (found.size() < 12) {
        completed = true;
      } else {
        try {
          Thread.sleep(250);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      players.addAll(found);
      page++;
    }
    return players;
  }

  private List<AuctionInfo> findPlayers(long playerId, Integer lowBound, int page) {
    try {
      Optional<TradeStatus> maybe = fifaRequests.searchPlayer(playerId, lowBound, page);
      return maybe.map(TradeStatus::getAuctionInfo).orElse(Collections.emptyList());
    } catch (Exception e) {
      logController.error(e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public MarketInfo getMarketInfo() {
    MarketInfo info = new MarketInfo();
    info.setMaxPurchases(maxPurchaseAmount);
    info.setPhishingToken(fifaRequests.getPhishingTokenForCheck());
    info.setSessionId(fifaRequests.getSessionForCheck());
    info.setAutoBuyEnabled(autoBuyEnabled);

    return info;
  }

  @Override
  public void setMarketInfo(MarketInfo info) {
//    this.maxPurchaseAmount = info.getMaxPurchases();
    fifaRequests.setPhishingToken(info.getPhishingToken());
    fifaRequests.setSessionId(info.getSessionId());
  }

  @Override
  public void updateTokens(String sessionId, String phishingToken, Boolean external) {
    if (external != null) {
      UrlResolver.externalUrl = external;
    }
    if (!phishingToken.equals(fifaRequests.getPhishingTokenForCheck())) {
      fifaRequests.setPhishingToken(phishingToken);
      logController.info("Updated phishingToken to " + phishingToken);
    }
    if (!sessionId.equals(fifaRequests.getSessionForCheck())) {
      fifaRequests.setSessionId(sessionId);
      logController.info("Updated sessionId to " + sessionId);
    }
  }

  @Override
  public void autoBuy(AutobuyRequest request) {
    if (request.getEnabled() == null) {
      return;
    }
    this.autoBuyEnabled = request.getEnabled();
    if (autoBuyEnabled) {
      purchases = 0;
      failed = false;
      maxPurchaseAmount = request.getPurchases();
      checkMarket();
    }
  }

  @Override
  public void deleteFromAutoBuy(Long id) {
    statisticRepository.delete(id);
  }

  @Override
  public PlayerStatistic getMinPrice(Long id) {
    return statisticRepository.findOne(id);
  }

  @Override
  public List<PlayerInfo> getAllToAutoBuy() {
    List<PlayerTradeStatus> all = tradeRepository.findAll();


    Map<Long, PlayerStatistic> idMinPrice = getStatsMap();
    List<Long> ids = all.stream().map(PlayerTradeStatus::getId).collect(Collectors.toList());
    List<PlayerProfile> profiles = playerProfileService.getAll(ids);
    Map<Long, PlayerProfile> profileMap = new HashMap<>();
    profiles.forEach(p -> profileMap.put(p.getId(), p));

    List<PlayerInfo> infos = new ArrayList<>();
    all.forEach(p -> {
      PlayerStatistic stats = idMinPrice.get(p.getId());
      if (stats != null) {
        List<PlayerStatistic.PriceDistribution> prices = stats.getPrices();
        if (prices != null && !prices.isEmpty()) {
          p.setMinMarketPrice(prices.get(0).getPrice());
        }
        p.setLastUpdate(stats.getDate());
        infos.add(new PlayerInfo(p, profileMap.get(p.getId())));
      }
    });


    return infos;
  }

  private Map<Long, Integer> getMinPricesMap() {
    List<PlayerStatistic> stats = statisticRepository.findAll();
    Map<Long, Integer> idMinPrice = new HashMap<>();
    stats.forEach(s -> {
      if (!s.getPrices().isEmpty()) {
        idMinPrice.put(s.getId(), s.getPrices().get(0).getPrice());
      }
    });
    return idMinPrice;
  }

  private Map<Long, PlayerStatistic> getStatsMap() {
    List<PlayerStatistic> stats = statisticRepository.findAll();
    Map<Long, PlayerStatistic> idMinStats = new HashMap<>();
    stats.forEach(s -> {
      idMinStats.put(s.getId(), s);
    });
    return idMinStats;
  }

  @Override
  public PlayerTradeStatus getOnePlayer(Long id) {
    return tradeRepository.findOne(id);
  }

  @Override
  public PlayerInfo getPlayerInfo(Long id) {
    PlayerTradeStatus tradeStatus = tradeRepository.findOne(id);
    PlayerProfile profile = playerProfileService.findOne(id);
    return new PlayerInfo(tradeStatus, profile);
  }

  @Override
  public void updateAutoBuy(BuyRequest request) {
    PlayerTradeStatus playerTradeStatus = tradeRepository.findOne(request.getId());
    playerTradeStatus.setEnabled(request.getEnabled());
    playerTradeStatus.updateDate();
    tradeRepository.save(playerTradeStatus);
  }

  @Override
  public void updatePlayer(PlayerTradeStatus request) {
    PlayerTradeStatus toUpdate = tradeRepository.findOne(request.getId());
    if (toUpdate == null) {
      toUpdate = createNewTrade(request);
    }
    toUpdate.setSellStartPrice(request.getSellStartPrice());
    toUpdate.setSellBuyNowPrice(request.getSellBuyNowPrice());
    toUpdate.setMaxPrice(request.getMaxPrice());
    toUpdate.updateDate();
    tradeRepository.save(toUpdate);
  }

  @Override
  public List<AuctionInfo> getTransferTargets() {
    return fifaRequests.getWatchlist().getAuctionInfo();
  }

  @Override
  public void removeExpired(List<AuctionInfo> expired) {
    expired.forEach(x -> removeFromTargets(x.getTradeId()));
  }


  @Override
  public void removeFromTargets(Long tradeId) {
    fifaRequests.removeFromTargets(tradeId);
  }

  @Override
  public TradeStatus getTradeStatus(Long tradeId) {
    return fifaRequests.getTradeStatus(tradeId);
  }

  @Override
  public BidStatus makeBid(Long tradeId, Long price) {
    BidStatus bidStatus = fifaRequests.makeBid(tradeId, price);

    System.out.println("Bid response status: " + bidStatus.getStatus());
    return bidStatus;
  }

  private PlayerTradeStatus createNewTrade(PlayerTradeStatus request) {
    PlayerProfile profile = playerProfileService.findOne(request.getId());
    PlayerTradeStatus tradeStatus = new PlayerTradeStatus();
    tradeStatus.setId(request.getId());
    tradeStatus.setName(profile.getName());
    tradeStatus.setEnabled(false);
    return tradeStatus;
  }

  @Override
  public synchronized List<TradeInfo> search(MarketSearchRequest searchRequest) {
    Optional<TradeStatus> search = fifaRequests.search(searchRequest);
    if (!search.isPresent()) {
      return Collections.emptyList();
    }

    List<AuctionInfo> auctionInfo = search.get().getAuctionInfo();
    Set<Long> ids = auctionInfo.stream()
        .map(x -> x.getItemData().getAssetId())
        .collect(Collectors.toSet());
    Map<Long, PlayerProfile> profiles = findProfiles(ids);
    Map<Long, PlayerTradeStatus> tradeStatuses = findTradeStatuses(ids);
    return auctionInfo.stream()
        .map(a -> new TradeInfo(a, tradeStatuses.get(a.getItemData().getAssetId()),
            profiles.get(a.getItemData().getAssetId())))
        .collect(Collectors.toList());
  }

  private Map<Long, PlayerTradeStatus> findTradeStatuses(Set<Long> ids) {
    Map<Long, PlayerTradeStatus> map = new HashMap<>();
    tradeRepository.findAll(ids).forEach(x -> map.put(x.getId(), x));
    return map;
  }

  private Map<Long, PlayerProfile> findProfiles(Set<Long> ids) {
    Map<Long, PlayerProfile> map = new HashMap<>();
    profileRepository.findAll(ids).forEach(x -> map.put(x.getId(), x));
    return map;
  }

  @Override
  public void addToAutoBid(AutobidRequest bidRequest) {
    Long playerId = bidRequest.getPlayerId();
    PlayerTradeStatus playerTradeStatus = tradeRepository.findOne(playerId);
    if (playerTradeStatus == null) {
      playerTradeStatus = createNewTrade(bidRequest);
    }
    logController.info("Added to autobid " + playerTradeStatus.getName() + ". Max price : " +
        playerTradeStatus.getMaxPrice());
  }

  private PlayerTradeStatus createNewTrade(AutobidRequest bidRequest) {
    Long playerId = bidRequest.getPlayerId();
    PlayerProfile profile = playerProfileService.findOne(playerId);
    PlayerTradeStatus tradeStatus = new PlayerTradeStatus();
    tradeStatus.setId(playerId);
    tradeStatus.setName(profile.getName());
    tradeStatus.setMaxPrice(bidRequest.getMaxBid());
    tradeRepository.save(tradeStatus);
    return tradeStatus;
  }

  @Override
  protected Integer getPurchasesRemained() {
    return maxPurchaseAmount - purchases;
  }
}
