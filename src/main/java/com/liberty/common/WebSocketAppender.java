package com.liberty.common;

import ch.qos.logback.core.AppenderBase;
import org.slf4j.event.LoggingEvent;

/**
 * User: Dimitr Date: 18.06.2016 Time: 11:22
 */
public class WebSocketAppender extends AppenderBase<LoggingEvent> {


    @Override
    protected void append(LoggingEvent event) {
        System.out.println(event.getMessage());
    }
}
