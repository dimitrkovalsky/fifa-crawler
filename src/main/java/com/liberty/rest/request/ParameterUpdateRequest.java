package com.liberty.rest.request;

import lombok.Data;

/**
 * User: Dimitr
 * Date: 20.11.2016
 * Time: 9:13
 */
@Data
public class ParameterUpdateRequest {
    private Boolean robotEnabled;
    private Boolean autoBuyEnabled;
    private Boolean autoSellEnabled;
    private Boolean noActivityEnabled;
    private Boolean autoSellRelistMinerEnabled;
    private Boolean autoTradeEnabled;
    private Boolean autoTradeOnlyActivePlayer;
}
