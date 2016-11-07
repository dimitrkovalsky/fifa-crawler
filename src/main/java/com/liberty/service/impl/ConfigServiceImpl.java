package com.liberty.service.impl;

import com.liberty.model.MarketConfig;
import com.liberty.repositories.MarketConfigRepository;
import com.liberty.repositories.PlayerTradeStatusRepository;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.ConfigService;
import com.liberty.service.PriceService;
import com.liberty.service.TagService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {

  private static final int MARKET_CONFIG_KEY = 1;

  @Autowired
  private TagService tagService;

  @Autowired
  private PriceService priceService;

  @Autowired
  private PlayerTradeStatusRepository tradeStatusRepository;

  @Autowired
  private TradeService tradeService;

  @Autowired
  private MarketConfigRepository configRepository;

  @Autowired
  private PriceHistoryRepository priceHistoryRepository;

  @Override
  public Map<String, Integer> getTagDistribution() {
    return tagService.getTagDistribution();
  }

  @Override
  public Set<String> getActiveTags() {
    return tradeService.getActiveTags();
  }

  @Override
  public void deactivateTag(String tag) {
    Set<String> activeTags = tradeService.getActiveTags();
    activeTags.remove(tag);
    MarketConfig config = getMarketConfig();
    config.setActiveTags(activeTags);
    configRepository.save(config);
    tagService.disableByTag(tag, activeTags);

  }

  @Override
  public MarketConfig getMarketConfig() {
    MarketConfig config = configRepository.findOne(MARKET_CONFIG_KEY);
    if (config == null) {
      config = new MarketConfig();
      config.setId(MARKET_CONFIG_KEY);
      configRepository.save(config);
    }
    return config;
  }

  @Override
  public void activateTag(String tag) {
    Set<String> activeTags = tradeService.getActiveTags();
    activeTags.add(tag);
    MarketConfig config = getMarketConfig();
    config.setActiveTags(activeTags);
    configRepository.save(config);

    tagService.enableByTag(tag);
  }

  @Override
  public void updateActivePlayersPrices() {
    priceService.updatePriceDistribution(true);
  }

  @Override
  public void cleanHistory() {
    AtomicInteger removed = new AtomicInteger();
    priceHistoryRepository.findAll().forEach(x -> {
      List<Long> toRemove = new ArrayList<>();
      x.getHistory().forEach((k, v) -> {
        if (v == null || v.isEmpty()) {
          toRemove.add(k);
        }
      });
      toRemove.forEach(r -> {
        x.getHistory().remove(r);
        removed.incrementAndGet();
      });
      priceHistoryRepository.save(x);

    });

    log.info("Removed " + removed.get() + " empty history records");
  }
}
