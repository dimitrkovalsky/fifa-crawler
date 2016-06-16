package com.liberty.rest.request;

import lombok.Data;

/**
 * User: Dimitr Date: 16.06.2016 Time: 8:55
 */
@Data
public class AddToBayRequest {

  private String name;
  private Integer id;
  private Integer maxPrice;
}
