package com.liberty.rest.request;

import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.10.2016.
 */
@Data
public class AuthRequest {

  private String protocol;
  private String ipPort;
  private String serverTime;
  private String lastOnlineTime;
  private String sid;
  private List<TokenUpdateRequest.Cookie> cookies;
}
