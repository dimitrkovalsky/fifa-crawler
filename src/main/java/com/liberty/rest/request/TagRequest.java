package com.liberty.rest.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * User: Dimitr
 * Date: 23.10.2016
 * Time: 10:38
 */
@Data
public class TagRequest {

  @NotNull
  private Long playerId;

  @NotNull
  private String tag;
}
