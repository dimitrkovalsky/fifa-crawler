package com.liberty.rest.request;

import lombok.Data;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 24.06.2016.
 */
@Data

public class TokenUpdateRequest {

    private String sessionId;
    private String token;
    private Boolean external = false;
    private List<Cookie> cookies;

    @Data
    public static class Cookie {

        private String name;
        private String value;
        private String expires;
        private Boolean httpOnly;
        private Boolean secure;

    }
}
