package com.liberty.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liberty.model.market.TradeStatus;

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
  @JsonIgnore
  private TradeStatus info;


  public enum Status {
    OK, FAIL;
  }
}
