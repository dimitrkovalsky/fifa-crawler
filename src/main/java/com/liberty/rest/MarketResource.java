package com.liberty.rest;

import com.liberty.model.MarketInfo;
import com.liberty.model.PlayerInfo;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.PlayerStatistic;
import com.liberty.rest.request.AutobuyRequest;
import com.liberty.rest.request.BuyRequest;
import com.liberty.rest.request.IdRequest;
import com.liberty.rest.request.SearchRequest;
import com.liberty.robot.AuctionRobot;
import com.liberty.service.TagService;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

  @Autowired
  private TagService tagService;

  @Autowired
  private AuctionRobot auctionRobot;

  @RequestMapping(path = "/info", method = RequestMethod.GET)
  public MarketInfo getInfo() {
    MarketInfo marketInfo = tradeService.getMarketInfo();
    marketInfo.setRobotEnabled(!auctionRobot.isDisabled());
    return marketInfo;
  }

  @RequestMapping(path = "/info", method = RequestMethod.POST)
  public void setInfo(MarketInfo info) {
    tradeService.setMarketInfo(info);
  }

  @RequestMapping(path = "/autobuy", method = RequestMethod.POST)
  public void autoBuy(@RequestBody AutobuyRequest request) {
    tradeService.autoBuy(request);
    auctionRobot.setEnabled(request.getRobotEnabled());
  }

  @RequestMapping(path = "/autobuy/player", method = RequestMethod.POST)
  public void updateAutoBuy(@RequestBody BuyRequest request) {
    tradeService.updateAutoBuy(request);
  }

  @RequestMapping(path = "/player/update", method = RequestMethod.POST)
  public void updatePlayer(@RequestBody PlayerTradeStatus request) {
    tradeService.updatePlayer(request);
  }

  @RequestMapping(path = "/player", method = RequestMethod.GET)
  public List<PlayerInfo> getAll() {

    return tradeService.getAllToAutoBuy();
  }

  @RequestMapping(path = "/search", method = RequestMethod.GET)
  public List<PlayerTradeStatus> getAll(SearchRequest request) {
    return tradeService.search(request.getPhrase());
  }

  @RequestMapping(path = "/player/{id}", method = RequestMethod.GET)
  public PlayerInfo getPlayerInfo(@PathVariable Long id) {
    return tradeService.getPlayerInfo(id);
  }

  @RequestMapping(path = "/player/{id}", method = RequestMethod.DELETE)
  public void removePlayer(@PathVariable Long id) {
    tradeService.deleteFromAutoBuy(id);
  }

  @RequestMapping(path = "/player/{id}/min", method = RequestMethod.GET)
  public PlayerStatistic getMin(@PathVariable Long id) {
    return tradeService.getMinPrice(id);
  }

  @RequestMapping(path = "/player/min", method = RequestMethod.POST)
  public PlayerStatistic findMin(@RequestBody IdRequest request) {
    if (request.getId().equals(-1L)) {
      tradeService.findMinPriceAll();
//      tagService.executeUpdate();
      return null;
    }
    return tradeService.findMinPrice(request.getId());
  }
}
