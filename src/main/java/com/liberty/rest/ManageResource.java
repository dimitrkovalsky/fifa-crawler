package com.liberty.rest;

import com.liberty.rest.request.AuthRequest;
import com.liberty.rest.request.TokenUpdateRequest;
import com.liberty.service.RequestService;
import com.liberty.service.TradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private RequestService requestService;

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/token", method = RequestMethod.POST)
    public void update(@RequestBody TokenUpdateRequest request) {
        requestService.updateTokens(request.getSessionId(), request.getToken(), request.getCookies());
        log.info("Updating tokens . Session : " + request.getSessionId() + ". Token : " + request
                .getToken());
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(path = "/auth", method = RequestMethod.POST)
    public void onAuth(@RequestBody AuthRequest request) {
        requestService.updateAuthTokens(request.getSid(), request.getCookies());
        log.info("Updated Auth. Session : " + request.getSid());
    }

}
