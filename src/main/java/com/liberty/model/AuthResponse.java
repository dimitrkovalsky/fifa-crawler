package com.liberty.model;

import lombok.Data;

/**
 * User: Dimitr Date: 04.06.2016 Time: 16:02
 */
@Data
public class AuthResponse {

    private String protocol;
    private String ipPort;
    private String serverTime;
    private String lastOnlineTime;
    private String sid;
}
