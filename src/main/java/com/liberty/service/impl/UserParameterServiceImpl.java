package com.liberty.service.impl;

import com.liberty.listeners.ParameterUpdateListener;
import com.liberty.model.UserParameters;
import com.liberty.repositories.UserParameterRepository;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.service.UserParameterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitr
 * Date: 19.11.2016
 * Time: 13:07
 */
@Service
@Slf4j
public class UserParameterServiceImpl implements UserParameterService {

    public static final long DEFAULT_USER = 1L;

    @Autowired
    private UserParameterRepository parameterRepository;

    private List<ParameterUpdateListener> listeners = new ArrayList<>();

    @Override
    public UserParameters getUserParameters() {
        UserParameters params = parameterRepository.findOne(DEFAULT_USER);
        if (params == null) {
            params = new UserParameters();
            params.setUserId(DEFAULT_USER);
            parameterRepository.save(params);
        }
        return params;
    }

    @Override
    public void saveParameters(UserParameters userParameters) {
        parameterRepository.save(userParameters);
    }


    @Override
    public void updateParameters(ParameterUpdateRequest request) {
        UserParameters parameters = parameterRepository.findOne(DEFAULT_USER);
        if (request.getNoActivityEnabled() != null) {
            parameters.setNoActivityEnabled(request.getNoActivityEnabled());
        }
        if (request.getRobotEnabled() != null)
            parameters.setRobotEnabled(request.getRobotEnabled());
        if (request.getAutoSellEnabled() != null)
            parameters.setAutoSellEnabled(request.getAutoSellEnabled());
        if (request.getAutoBuyEnabled() != null)
            parameters.setAutoBuyEnabled(request.getAutoBuyEnabled());
        if (request.getAutoSellRelistMinerEnabled() != null)
            parameters.setAutoSellRelistMinerEnabled(request.getAutoSellRelistMinerEnabled());
        if (request.getAutoTradeEnabled() != null)
            parameters.setAutoTradeEnabled(request.getAutoTradeEnabled());
        if (request.getAutoTradeOnlyActivePlayer() != null)
            parameters.setAutoTradeOnlyActivePlayer(request.getAutoTradeOnlyActivePlayer());

        log.info("Updating parameters to : " + request);
        parameterRepository.save(parameters);
        listeners.forEach(x -> x.onParameterUpdate(request));
    }


    @Override
    public void subscribe(ParameterUpdateListener listener) {
        listeners.add(listener);
    }


}
