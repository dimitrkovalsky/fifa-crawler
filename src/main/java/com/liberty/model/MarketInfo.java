package com.liberty.model;

import lombok.Data;

/**
 * User: Dimitr Date: 16.06.2016 Time: 8:36
 */
@Data
public class MarketInfo {

    private Integer maxPurchases;
    private String sessionId;
    private String phishingToken;
    private Boolean autoBuyEnabled;
    private Boolean robotEnabled;
    private Integer rate;
}
