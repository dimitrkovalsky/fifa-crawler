package com.liberty.robot;

import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: Dimitr
 * Date: 15.10.2016
 * Time: 14:14
 */
@Component
public class AuctionRobot {

  @Autowired
  private TradeService service;

  public void trade(){
   // service.getTransferTargets();
  }
}
