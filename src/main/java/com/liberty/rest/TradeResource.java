package com.liberty.rest;

import com.liberty.model.market.ItemData;
import com.liberty.rest.request.SellRequest;
import com.liberty.rest.response.SizeResponse;
import com.liberty.service.TradeService;

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
  public List<ItemData> getUnassigned() {
    return tradeService.getUnassigned();
  }

  @RequestMapping(path = "/tradepile", method = RequestMethod.GET)
  public SizeResponse getTradePileSize() {
    return new SizeResponse(tradeService.getTradePileSize());
  }

  @RequestMapping(method = RequestMethod.POST)
  public void sell(@RequestBody SellRequest request) {
    tradeService.sell(request);
  }


}
