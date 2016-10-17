package com.liberty.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.10.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidStatus {

  private Status status;
  private int errorCode;
  private long tradeId;


  public enum Status {
    OK, FAIL;
  }
}
