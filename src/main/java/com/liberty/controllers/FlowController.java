package com.liberty.controllers;

import com.liberty.model.UserParameters;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.service.NoActivityService;
import com.liberty.service.UserParameterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Queue;
import java.util.Timer;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.11.2016.
 */
@Service
@Slf4j
public class FlowController implements InitializingBean {

    private Optional<UserParameters> lastParameters = Optional.empty();

    @Autowired
    private UserParameterService parameterService;

    @Autowired
    private NoActivityService noActivityService;

    private volatile boolean suspended;

    @PreDestroy
    private void restoreParameters() {
        if (!suspended)
            return;
        lastParameters.ifPresent(x -> parameterService.updateParameters(ParameterUpdateRequest.fromParameters(x)));
        lastParameters = Optional.empty();
    }

    private void backupCurrentParameters() {
        UserParameters userParameters = parameterService.getUserParameters();
        lastParameters = Optional.of(userParameters);
    }

    private void runNoWork() {
        if (suspended) {
            log.info("[FlowController] can not run NoWork on suspended system");
            return;
        }
        if (!parameterService.getUserParameters().isNoActivityEnabled()) {
            log.info("[FlowController] NoWork was disabled manually");
            return;
        }
        if (!isPendingUpdate()){
            log.info("[FlowController] No players pending for update");
            return;
        }
        // TODO: fix it
        backupCurrentParameters();
        UserParameters parameters = new UserParameters();
        parameters.disableAll();
        parameters.setNoActivityEnabled(true);
        parameterService.updateParameters(ParameterUpdateRequest.fromParameters(parameters));

    }

    private boolean isPendingUpdate() {
        Queue<Long> queue = noActivityService.getPendingQueue();
        if (queue == null || queue.isEmpty())
            return false;
        return true;
    }

    private void init() {
        FlowConfig config = getFlowConfig();
        Timer timer = new Timer();
        // timer.schedule();
    }

    private void resumeSystem() {
        log.info("[FlowController] trying to resume system flow...");
        restoreParameters();
        suspended = false;
        log.info("[FlowController] system flow resumed...");
    }

    private void suspendSystem() {
        if (suspended)
            return;
        log.info("[FlowController] trying to suspend system flow...");
        backupCurrentParameters();
        UserParameters parameters = new UserParameters();
        parameters.disableAll();
        parameterService.updateParameters(ParameterUpdateRequest.fromParameters(parameters));
        suspended = true;
        log.info("[FlowController] system flow suspended...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private FlowConfig getFlowConfig() {
        return new FlowConfig();
    }

    @Data
    public static class FlowConfig {
        public int sleepAfterMinutes = 3;
        public int sleepDurationMinutes = 1;
        public int noActivityActivateEvery = 5;
    }
}
