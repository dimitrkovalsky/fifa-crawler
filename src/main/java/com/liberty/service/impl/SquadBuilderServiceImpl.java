package com.liberty.service.impl;

import com.liberty.common.DateHelper;
import com.liberty.common.DelayHelper;
import com.liberty.common.PriceHelper;
import com.liberty.model.FullSquad;
import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerStatistic;
import com.liberty.model.Squad;
import com.liberty.model.SquadPlayer;
import com.liberty.model.SquadStatistic;
import com.liberty.model.market.ItemData;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.repositories.SquadRepository;
import com.liberty.repositories.SquadStatisticRepository;
import com.liberty.rest.request.BuyAllPlayersRequest;
import com.liberty.rest.request.BuySinglePlayerRequest;
import com.liberty.service.CrawlerService;
import com.liberty.service.PriceService;
import com.liberty.service.RequestService;
import com.liberty.service.SquadBuilderService;
import com.liberty.service.TradeService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.DateHelper.toReadableString;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Service
@Slf4j
public class SquadBuilderServiceImpl implements SquadBuilderService {

  @Autowired
  private CrawlerService crawlerService;

  @Autowired
  private PriceService priceService;

  @Autowired
  private SquadRepository squadRepository;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Autowired
  private SquadStatisticRepository squadStatisticRepository;

  @Autowired
  private RequestService requestService;

  @Autowired
  private LogController logController;

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;


  @Autowired
  private TradeService tradeService;

  private Map<Long, ItemData> getMyPlayers() {
    Map<Long, ItemData> map = new HashMap<>();

    requestService.getMyPlayers().forEach(x -> map.put(x.getAssetId(), x));
    tradeService.getUnassigned().forEach(x -> map.put(x.getPlayerId(), x.getItems().get(0)));
    return map;
  }

  @Override
  public FullSquad fetchPricesForSquad(Long squadId) {
    Squad stored = squadRepository.findOne(squadId);
    if (stored != null) {
      return useStoredSquad(stored);
    }

    return updateSquad(squadId);
  }

  @Override
  public FullSquad updateSquad(Long squadId) {
    List<PlayerProfile> profilesBySquad = crawlerService.findProfilesBySquad(squadId);
    Squad squadInfo = crawlerService.fetchBaseSquadInfo(squadId);
    log.info("Found " + profilesBySquad.size() + " players for squad " + squadId + " (" +
        squadInfo.getSquadName() + ")");
    FullSquad fullSquad = new FullSquad();
    fullSquad.setSquadId(squadId);
    List<SquadPlayer> players = new ArrayList<>();
    long squadMinPrice = 0;
    long squadMedian = 0;
    int playerCount = 0;
    Map<Long, ItemData> myPlayers = getMyPlayers();
    for (PlayerProfile profile : profilesBySquad) {
      PlayerStatistic price = priceService.findMinPriceForSBC(profile.getId());
      PlayerStatistic.PriceDistribution minPrice = PriceHelper.getMinPrice(price);
      long median = PriceHelper.getMedian(price);
      String lastUpdate = DateHelper.toReadableString(price.getInnerDate());
      squadMedian += median;
      squadMinPrice += minPrice.getPrice();
      players.add(new SquadPlayer(profile.getId(), profile, minPrice, median, myPlayers
          .containsKey(profile.getId()), tradeStatusRepository.findOne(profile.getId()), lastUpdate));
      playerCount++;
      logController.info("Updated " + playerCount + " / " + profilesBySquad.size() + " players " +
          "from squad "
          + squadInfo.getSquadName());
      DelayHelper.wait(7000, 777);
    }
    fullSquad.setPlayers(players);
    fullSquad.setPrice(new PriceHelper.HistoryPoint(squadMinPrice, squadMedian));
    fullSquad.setDate(toReadableString(LocalDateTime.now()));
    fullSquad.setSquadName(squadInfo.getSquadName());
    fullSquad.setSquadGroup(squadInfo.getSquadGroup());
    saveSquad(fullSquad);
    updateHistory(fullSquad);
    return fullSquad;
  }

  private void updateHistory(FullSquad fullSquad) {
    Long squadId = fullSquad.getSquadId();
    SquadStatistic statistic = squadStatisticRepository.findOne(squadId);
    if (statistic == null) {
      statistic = new SquadStatistic();
      statistic.setId(squadId);
    }

    statistic.getHistory().put(System.currentTimeMillis(), fullSquad.getPrice());
    squadStatisticRepository.save(statistic);
  }

  @Override
  public void buySquad(Long squadId) {
    Squad squad = squadRepository.findOne(squadId);
    if (squad == null) {
      log.error("Can not find squad with id : " + squadId);
      return;
    }

  }

  private void saveSquad(FullSquad fullSquad) {
    Squad squad = new Squad();
    squad.setId(fullSquad.getSquadId());
    squad.setInnerDate(LocalDateTime.now());
    List<Long> ids = fullSquad.getPlayers().stream()
        .map(SquadPlayer::getPlayerId)
        .collect(Collectors.toList());
    squad.setSquadGroup(fullSquad.getSquadGroup());
    squad.setSquadName(fullSquad.getSquadName());
    squad.setPlayerIds(ids);
    squadRepository.save(squad);
  }

  private FullSquad useStoredSquad(Squad stored) {
    FullSquad squad = new FullSquad();
    squad.setSquadId(stored.getId());
    long squadMinPrice = 0;
    long squadMedian = 0;
    Map<Long, ItemData> myPlayers = getMyPlayers();
    List<SquadPlayer> players = new ArrayList<>();
    for (Long id : stored.getPlayerIds()) {
      PlayerProfile profile = profileRepository.findOne(id);
      PlayerStatistic price = priceService.getMinPrice(id);
      PlayerStatistic.PriceDistribution minPrice = PriceHelper.getMinPrice(price);
      long median = PriceHelper.getMedian(price);
      String lastUpdate = DateHelper.toReadableString(price.getInnerDate());
      squadMedian += median;
      squadMinPrice += minPrice.getPrice();
      players.add(new SquadPlayer(profile.getId(), profile, minPrice, median, myPlayers
          .containsKey(profile.getId()), tradeStatusRepository.findOne(profile.getId()), lastUpdate));
    }
    squad.setPlayers(players);
    squad.setPrice(new PriceHelper.HistoryPoint(squadMinPrice, squadMedian));
    squad.setDate(stored.getDate());
    return squad;
  }

  @Override
  public boolean buyPlayer(BuySinglePlayerRequest request) {
    return tradeService.buyPlayer(request.getPlayerId(), request.getMaxPrice(), request
        .getPlayerName());
  }

  @Override
  public void buyAllPlayers(BuyAllPlayersRequest request) {
    log.info("Trying to buy " + request.getPlayers().size() + " players");
    int count = 0;
    for (BuySinglePlayerRequest playerRequest : request.getPlayers()) {
      boolean success = buyPlayer(playerRequest);
      count++;
      log.info("Bought " + count + " / " + request.getPlayers().size() + " players");
      if (!success)
        break;
    }
  }
}
