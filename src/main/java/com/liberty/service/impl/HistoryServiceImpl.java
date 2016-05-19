package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
@Service
@Slf4j
public class HistoryServiceImpl implements HistoryService {

  @Autowired
  private PriceHistoryRepository historyRepository;

  @Override
  public void recordHistory(PlayerProfile profile) {
    recordHistory(profile.getId(), profile.getPrice());
  }

  @Override
  public void recordHistory(long playerId, Price price) {
    PriceHistory priceHistory = historyRepository.findOne(playerId);
    if (priceHistory == null) {
      historyRepository.save(createNew(playerId, price));
      log.info("HISTORY", "Saved new history record for player : " + playerId);
      return;
    }
    PriceHistory.PriceRecord previous = priceHistory.getCurrentPrice();

    if (previous.getPrice().equals(price)) {
      processNoChanges(priceHistory, price);
      log.info("HISTORY", "Price does'n change for player : " + playerId);
      return;
    }

    PriceHistory.PriceRecord oldPrice = priceHistory.getLastPrice();
    if (oldPrice != null)
      priceHistory.addPrice(oldPrice);
    priceHistory.setLastPrice(previous);
    priceHistory.setCurrentPrice(new PriceHistory.PriceRecord(price));
    historyRepository.save(priceHistory);
    log.info("HISTORY", "Updated history record for player : " + playerId);
  }

  private void processNoChanges(PriceHistory history, Price newPrice) {
    history.setCurrentPrice(new PriceHistory.PriceRecord(newPrice));
    historyRepository.save(history);
  }

  private PriceHistory createNew(long playerId, Price price) {
    PriceHistory history = new PriceHistory();
    history.setId(playerId);
    history.setCurrentPrice(new PriceHistory.PriceRecord(price));
    history.setFirstPrice(new PriceHistory.PriceRecord(price));
    return history;
  }
}
