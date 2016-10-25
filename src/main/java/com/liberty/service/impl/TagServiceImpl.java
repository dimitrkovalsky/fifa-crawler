package com.liberty.service.impl;

import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.Tag;
import com.liberty.model.PlayerStatistic;
import com.liberty.repositories.PlayerStatisticRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.repositories.TagRepository;
import com.liberty.service.TagService;
import com.liberty.service.TradeService;
import com.liberty.websockets.LogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.PriceHelper.defineMaxBuyNowPrice;

/**
 * User: Dimitr
 * Date: 22.10.2016
 * Time: 10:59
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Autowired
  private PlayerStatisticRepository statisticRepository;

  @Autowired
  private LogController logController;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private TradeService tradeService;

  @Override
  public void executeUpdate() {
    String tag = "medium";
//    markTags();
 //   fetchMarketPrices(tag);
    enableByTag(tag);
    updatePrices(tag);
  }

  private void fetchMarketPrices(String tag) {
    List<PlayerTradeStatus> byTag = getListByTag(tag);
    log.info("Trying to update prices for " + byTag.size() + " players. Tagged by : " + tag);

    final int[] counter = {0};
    byTag.forEach(p -> {
      tradeService.findMinPrice(p.getId());
      counter[0]++;
      logController.info("Updated market price for " + counter[0] + " / " + byTag.size());
      DelayHelper.wait(7000, 500);
    });
  }

  private List<PlayerTradeStatus> getListByTag(String tag) {
    return tradeStatusRepository.findAll().stream()
        .filter(x -> x.getTags().contains(tag))
        .collect(Collectors.toList());
  }

  @Override
  public void addTag(Long playerId, String tag) {
    PlayerTradeStatus player = tradeStatusRepository.findOne(playerId);
    Tag currentTag = tagRepository.findOneByName(tag);
    if (currentTag == null) {
      logController.error("Can not add tag for " + player.getName() + ". No tag " + tag
          + " in storage");
      return;
    }
    if (!player.getTags().contains(tag)) {
      player.getTags().add(tag);
      tradeStatusRepository.save(player);
      logController.info("Successfully added tag : " + tag + " . For " + player.getName());
    } else {
      logController.error("Player : " + player.getName() + " contains tag : " + tag);
    }
  }

  @Override
  public void removeTag(Long playerId, String tag) {
    PlayerTradeStatus player = tradeStatusRepository.findOne(playerId);
    Tag currentTag = tagRepository.findOneByName(tag);
    if (currentTag == null) {
      logController.error("Can not add tag for " + player.getName() + ". No tag " + tag
          + " in storage");
      return;
    }
    if (!player.getTags().contains(tag)) {
      logController.info("Can not remove tag : " + tag + " . " + " Player " + player.getName() +
          " has no such tag");
    } else {
      player.getTags().remove(tag);
      tradeStatusRepository.save(player);
      logController.error("Successfully removed tag : " + tag + " from " + player.getName());
    }
  }

  @Override
  public void enableByTag(String tag) {
    AtomicInteger counter = new AtomicInteger(0);
    tradeStatusRepository.findAll().forEach(t -> {
      if (t.getTags().contains(tag)) {
        t.setEnabled(true);
        counter.incrementAndGet();
      } else {
        t.setEnabled(false);
      }
      tradeStatusRepository.save(t);
    });
    log.info("Enabled : " + counter.get() + " players for tag : " + tag);
  }

  @Override
  public void markTags() {
    Map<String, Integer> tagDistribution = new HashMap<>();
    List<PlayerTradeStatus> all = tradeStatusRepository.findAll();
    all.forEach(t -> {
      String tag = defineTag(t);
      addToMap(tagDistribution, tag);
      t.getTags().clear();
      t.getTags().add(tag);
      tradeStatusRepository.save(t);
    });
    log.info("Tagged " + all.size() + " players : ");
    tagDistribution.forEach((k, v) -> log.info("[" + k + "] --> " + v + " players"));
  }

  private void addToMap(Map<String, Integer> tagDistribution, String tag) {
    Integer count = tagDistribution.get(tag);
    if (count == null) {
      tagDistribution.put(tag, 1);
    } else {
      tagDistribution.put(tag, count + 1);
    }
  }

  private void updatePrices(String tag) {
    Map<Long, Integer> pricesMap = getMinPricesMap();
    AtomicInteger counter = new AtomicInteger(0);
    getListByTag(tag).stream()
        .map(p -> {
          p.setEnabled(false);
          Integer price = pricesMap.get(p.getId());
          if (price != null && price != 0) {
            p.setEnabled(true);
            p.setMaxPrice(defineMaxBuyNowPrice(price));
            counter.incrementAndGet();
          }
          p.updateDate();
          return p;
        }).forEach(tradeStatusRepository::save);
    log.info("Updated : " + counter.get() + " prices for tag : " + tag);
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


  private String defineTag(PlayerTradeStatus status) {
    Integer price = status.getMaxPrice();
    if (status.getRareflag() != null && status.getRareflag() > 2) {
      return "inform";
    }
    if (price <= 3000) {
      return "cheap";
    } else if (price <= 5000) {
      return "medium";
    } else if (price <= 10000) {
      return "rich";
    } else if (price <= 50000) {
      return "expensive";
    } else {
      return "top";
    }
  }


  @Override
  public List<PlayerInfo> getByTag(String tag) {
    return tradeService.getAllToAutoBuy().stream()
        .filter(p -> p.getTradeStatus().getTags().contains(tag))
        .collect(Collectors.toList());
  }

  @Override
  public List<Tag> getAllTags() {
    return tagRepository.findAll();
  }

}
