package com.liberty.service;

import com.liberty.listeners.ParameterUpdateListener;
import com.liberty.model.UserParameters;
import com.liberty.rest.request.ParameterUpdateRequest;

/**
 * User: Dimitr
 * Date: 19.11.2016
 * Time: 13:06
 */
public interface UserParameterService {

    UserParameters getUserParameters();

    void saveParameters(UserParameters userParameters);

    void updateParameters(ParameterUpdateRequest request);

    void subscribe(ParameterUpdateListener listener);
}
