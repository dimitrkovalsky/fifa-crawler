package com.liberty.websockets;

import com.liberty.common.MessageType;

import org.apache.log4j.lf5.LogLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 22.06.2016.
 */
@Data
@AllArgsConstructor
public class LogMessage extends BaseMessage {

  private String message;
  private LogLevel level;

  @Override
  public String getMessageType() {
    return MessageType.LOG_MESSAGE;
  }
}
