package com.liberty.websockets;

import com.liberty.model.MonitoringResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 22.05.2016 Time: 21:36
 */
@Controller
@Slf4j
public class LiveController {

  @Autowired
  private SimpMessagingTemplate template;

  @SendTo("/topic/live")
  @MessageMapping("/updates")
  public int onUpdate(String message) {
    log.info("Message : " + message);
    return 0;
  }

  public void onPriceChanged(MonitoringResult monitoringResult) {
    log.info("Changed : " + monitoringResult);
    send(monitoringResult);

  }

  private void send(Object data) {
    if (template != null)
      template.convertAndSend("/topic/live", data);
    else
      log.error("Client doesn't connected");
  }


  public void live(String data) {
    log.info("Live connected : " + data);
  }
}
