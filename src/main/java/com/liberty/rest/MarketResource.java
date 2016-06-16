package com.liberty.rest;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.market.PlayerStatistic;
import com.liberty.rest.request.AddToBayRequest;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 16.06.2016 Time: 8:47
 */
@RestController
@RequestMapping("/api/market")
@Slf4j
public class MarketResource {

  @Autowired
  private TradeService tradeService;

  @RequestMapping(path = "/info", method = RequestMethod.GET)
  public MarketInfo getInfo() {
    return tradeService.getMarketInfo();
}

  @RequestMapping(path = "/info", method = RequestMethod.POST)
  public void setInfo(MarketInfo info) {
    tradeService.setMarketInfo(info);
  }

  @RequestMapping(path = "/autobuy", method = RequestMethod.POST)
  public void autoBuy(boolean run) {
    tradeService.autoBuy(run);
  }

  @RequestMapping(path = "/player", method = RequestMethod.POST)
  public void updatePlayer(AddToBayRequest request) {
    tradeService.addToAutoBuy(request.getName(), request.getId(), request.getMaxPrice());
  }

  @RequestMapping(path = "/player", method = RequestMethod.GET)
  public List<PlayerTradeStatus> getAll() {
    return tradeService.getAllToAutoBuy();
  }

  @RequestMapping(path = "/player/{id}", method = RequestMethod.DELETE)
  public void removePlayer(@PathVariable Long id) {
    tradeService.deleteFromAutoBuy(id);
  }

  @RequestMapping(path = "/player/{id}/min", method = RequestMethod.GET)
  public PlayerStatistic getMin(@PathVariable Long id) {
    return tradeService.getMinPrice(id);
  }

  @RequestMapping(path = "/player/{id}/min", method = RequestMethod.POST)
  public PlayerStatistic findMin(@PathVariable Long id) {
    return tradeService.findMinPrice(id);
  }
}
