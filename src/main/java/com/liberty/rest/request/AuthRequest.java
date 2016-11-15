package com.liberty.rest.request;

import lombok.Data;

import java.util.List;

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
