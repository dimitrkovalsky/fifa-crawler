package com.liberty.websockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 22.05.2016 Time: 21:36
 */
@Controller
@Slf4j
public class LiveController {

  @SendTo("/topic/live")
  public int onUpdate(String message) {
    log.info("Message : " + message);
    return 0;
  }
  public void cron(){
    onUpdate("dara");
  }

  @MessageMapping("/updates")
  public void live(String data) {
    log.info("Live connected : " + data);
  }
}
