package com.liberty.rest.request;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.10.2016.
 */
@Data
public class AutobidRequest {

    private Long tradeId;
    private Long playerId;
    private Integer maxBid;
}
