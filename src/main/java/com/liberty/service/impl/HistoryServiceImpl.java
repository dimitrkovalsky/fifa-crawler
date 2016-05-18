package com.liberty.service.impl;

import com.liberty.model.PlayerProfile;
import com.liberty.model.Price;
import com.liberty.model.PriceHistory;
import com.liberty.repositories.PriceHistoryRepository;
import com.liberty.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
@Service
public class HistoryServiceImpl implements HistoryService {

  @Autowired
  private PriceHistoryRepository historyRepository;

  @Override
  public void recordHistory(PlayerProfile profile) {
    recordHistory(profile.getId(), profile.getPrice());
  }

  public void recordHistory(long playerId, Price price) {
//    PriceHistory history = new PriceHistory();
    PriceHistory priceHistory = historyRepository.findOne(playerId);
    if (priceHistory == null) {
      createNew(playerId, price);
      return;
    }

    Price previous = priceHistory.getCurrentPrice();
    Price oldPrice = priceHistory.getLastPrice();
    if (oldPrice != null)
      priceHistory.addPrice(oldPrice);

  }

  private PriceHistory createNew(long playerId, Price price) {
    PriceHistory history = new PriceHistory();
    history.setId(playerId);
    history.setCurrentPrice(price);
    return history;
  }
}
