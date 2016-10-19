package com.liberty.rest;

import com.liberty.model.market.GroupedToSell;
import com.liberty.rest.request.AutobidRequest;
import com.liberty.rest.request.BidRequest;
import com.liberty.rest.request.SellRequest;
import com.liberty.rest.response.BidStatus;
import com.liberty.service.TradeService;
import com.liberty.websockets.BuyMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@RestController
@RequestMapping("/api/trade")
public class TradeResource {

  @Autowired
  private TradeService tradeService;

  @RequestMapping(method = RequestMethod.GET)
  public List<GroupedToSell> getUnassigned() {
    return tradeService.getUnassigned();
  }

  @RequestMapping(path = "/tradepile", method = RequestMethod.GET)
  public BuyMessage getTradePileSize() {
    return tradeService.getTradepileInfo();
  }

  @RequestMapping(path = "/bid", method = RequestMethod.POST)
  public BidStatus makeBid(@RequestBody BidRequest bidRequest) {
    return tradeService.makeBid(bidRequest.getTradeId(), bidRequest.getBid());
  }

  @RequestMapping(path = "/autobid", method = RequestMethod.GET)
  public void autoBid(@RequestBody AutobidRequest bidRequest) {
    tradeService.addToAutoBid(bidRequest);
  }

  @RequestMapping(method = RequestMethod.POST)
  public void sell(@RequestBody SellRequest request) {
    tradeService.sell(request);
  }


}
