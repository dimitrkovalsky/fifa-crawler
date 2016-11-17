package com.liberty.websockets;

import com.liberty.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.lf5.LogLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * User: Dimitr Date: 22.05.2016 Time: 21:36
 */
@Controller
@Slf4j
public class LogController {

    public static final String TOPIC_NAME = "/topic/live";
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private RequestService requestService;

    @SendTo(TOPIC_NAME)
    @MessageMapping("/updates")
    public int onUpdate(String message) {
        log.info("Message : " + message);
        return 0;
    }


    public void info(String toLog) {
        log.info(toLog + ". " + requestService.getRateString());
        send(new LogMessage(toLog, LogLevel.INFO));
    }


    public void error(String toLog) {
        log.error(toLog, ". " + requestService.getRateString());
        send(new LogMessage(toLog, LogLevel.ERROR));
    }

    private void send(BaseMessage msg) {
        if (template != null) {
            template.convertAndSend(TOPIC_NAME, msg);
        } else {
            log.error("Client doesn't connected");
        }
    }

    public void live(String data) {
        log.info("Live connected : " + data);
    }

    public void logBuy(int unassigned, int canSell, int credits, int purchasesRemained) {
        send(new BuyMessage(unassigned, canSell, credits, purchasesRemained));
    }

    public void sendRate(int rate) {
        send(new RateMessage(rate));
    }
}
