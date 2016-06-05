package com.liberty.model.market;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.06.2016.
 */
@Data
public class FifaError {

  private String reason;
  private String message;
  private Integer code;


  public interface ErrorCode {
    int SESSION_EXPIRED = 401;
  }
}
