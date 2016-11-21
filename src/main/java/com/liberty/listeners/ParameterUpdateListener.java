package com.liberty.listeners;

import com.liberty.rest.request.ParameterUpdateRequest;

/**
 * User: Dimitr
 * Date: 20.11.2016
 * Time: 9:12
 */
public interface ParameterUpdateListener {
    void onParameterUpdate(ParameterUpdateRequest request);
}
