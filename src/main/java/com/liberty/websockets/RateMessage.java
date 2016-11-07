package com.liberty.websockets;

import com.liberty.common.MessageType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 23.06.2016.
 */
@Data
@AllArgsConstructor
public class RateMessage extends BaseMessage {

  private Integer rate;

  @Override
  public String getMessageType() {
    return MessageType.RATE;
  }
}
