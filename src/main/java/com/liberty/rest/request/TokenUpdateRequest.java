package com.liberty.rest.request;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.06.2016.
 */
@Data

public class TokenUpdateRequest {

  private String sessionId;
  private String token;
  private Boolean external = false;
}
