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
  private String debug;
  private String string;


  public interface ErrorCode {
    int SESSION_EXPIRED = 401;
    int SESSION_FAILED = 461;
  }
}
