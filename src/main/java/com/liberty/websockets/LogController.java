package com.liberty.websockets;

import com.liberty.model.MonitoringResult;

import org.apache.log4j.lf5.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * User: Dimitr Date: 22.05.2016 Time: 21:36
 */
@Controller
@Slf4j
public class LogController {

  public static final String TOPIC_NAME = "/topic/live";
  @Autowired
  private SimpMessagingTemplate template;

  @SendTo(TOPIC_NAME)
  @MessageMapping("/updates")
  public int onUpdate(String message) {
    log.info("Message : " + message);
    return 0;
  }

  public void onPriceChanged(MonitoringResult monitoringResult) {
    log.info("Changed : " + monitoringResult);
    send(monitoringResult);
  }

  public void info(String toLog) {
    log.info(toLog);
    send(new LogMessage(toLog, LogLevel.INFO));
  }

  public void error(String toLog) {
    log.error(toLog);
    send(new LogMessage(toLog, LogLevel.ERROR));
  }

  private void send(Object data) {
    if (template != null)
      template.convertAndSend(TOPIC_NAME, data);
    else
      log.error("Client doesn't connected");
  }

  @Data
  @AllArgsConstructor
  private static class LogMessage {

    private String message;
    private LogLevel level;
  }


  public void live(String data) {
    log.info("Live connected : " + data);
  }
}
