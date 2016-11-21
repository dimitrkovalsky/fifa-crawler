package com.liberty.rest.request;

import com.liberty.model.UserParameters;
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

    public static ParameterUpdateRequest fromParameters(UserParameters parameters) {
        ParameterUpdateRequest request = new ParameterUpdateRequest();
        request.setAutoBuyEnabled(parameters.isAutoBuyEnabled());
        request.setRobotEnabled(parameters.isRobotEnabled());
        request.setAutoSellEnabled(parameters.isAutoSellEnabled());
        request.setNoActivityEnabled(parameters.isNoActivityEnabled());
        request.setAutoSellRelistMinerEnabled(parameters.isAutoSellRelistMinerEnabled());
        request.setAutoTradeEnabled(parameters.isAutoTradeEnabled());
        request.setAutoTradeOnlyActivePlayer(parameters.isAutoTradeOnlyActivePlayer());
        return request;
    }
}
