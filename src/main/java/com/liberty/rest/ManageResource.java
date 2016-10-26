package com.liberty.rest;

import com.liberty.rest.request.AuthRequest;
import com.liberty.rest.request.TokenUpdateRequest;
import com.liberty.service.TradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 22.05.2016 Time: 10:50
 */
@RestController
@RequestMapping("/api/manage")
@CrossOrigin(origins = "*")
@Slf4j
public class ManageResource {

  @Autowired
  private TradeService tradeService;

  @CrossOrigin(origins = "*")
  @RequestMapping(path = "/token", method = RequestMethod.POST)
  public void update(@RequestBody TokenUpdateRequest request) {
    tradeService.updateTokens(request.getSessionId(), request.getToken(), request.getExternal(),
        request.getCookies());
    log.info("Updating tokens . Session : " + request.getSessionId() + ". Token : " + request
        .getToken());
  }

  @CrossOrigin(origins = "*")
  @RequestMapping(path = "/auth", method = RequestMethod.POST)
  public void onAuth(@RequestBody AuthRequest request) {
    tradeService.updateAuth(request.getSid(), request.getCookies());
    log.info("Updated Auth. Session : " + request.getSid());
  }

}
