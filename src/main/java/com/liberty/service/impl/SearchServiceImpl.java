package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.TradeInfo;
import com.liberty.model.market.AuctionInfo;
import com.liberty.model.market.TradeStatus;
import com.liberty.repositories.PlayerProfileRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.rest.request.MarketSearchRequest;
import com.liberty.service.RequestService;
import com.liberty.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
@Service
public class SearchServiceImpl implements SearchService {

  @Autowired
  private RequestService requestService;

  @Autowired
  private PlayerTradeStatusRepository tradeRepository;

  @Autowired
  private PlayerProfileRepository profileRepository;

  @Override
  public synchronized List<TradeInfo> search(MarketSearchRequest searchRequest) {
    Optional<TradeStatus> search = requestService.search(searchRequest);
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
  public List<PlayerTradeStatus> search(String phrase) {
    return tradeRepository.findByName(phrase)
        .stream().limit(5)
        .collect(Collectors.toList());
  }
}
