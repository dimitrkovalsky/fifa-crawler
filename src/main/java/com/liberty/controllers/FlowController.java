package com.liberty.controllers;

import com.liberty.model.UserParameters;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.service.NoActivityService;
import com.liberty.service.UserParameterService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Queue;

import static com.liberty.controllers.State.*;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.11.2016.
 */
//@Component
@Slf4j
public class FlowController implements InitializingBean {

    private Optional<UserParameters> lastParameters = Optional.empty();

    @Autowired
    private UserParameterService parameterService;

    @Autowired
    private NoActivityService noActivityService;

    private volatile boolean suspended;
    private FlowConfig config;
    private State state = INITIALIZED;
    private volatile int currentStateMinutes = 0;
    private volatile int noSleepTime = 0;
    private volatile int sleepTime = 0;

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    private void onSchedule() {
        currentStateMinutes++;
        if (state != SLEEP)
            noSleepTime++;
        else
            sleepTime++;
        State nextState = defineNextState();  // TODO: check time for transaction
        if (nextState != state) {
            changeState(nextState);
            currentStateMinutes = 0;
            state = nextState;
            log.info("[FlowController] current state : " + state);
        }
    }

    private void changeState(State nextState) {
        log.info("[FlowController] trying to change state from " + state + " to " + nextState);
        switch (nextState) {
            case INITIALIZED:
                log.error("Can not change state to " + nextState);
                break;
            case SLEEP:
                suspendSystem();
                noSleepTime = 0;
                break;
            case ON_AUTO_BUY:
                runAutoBuy();
                break;
            case ON_NO_ACTIVITY:
                runNoActivity();
                break;
            case NEED_TRADEPILE_UPDATE:
                log.error("Can not change state to " + nextState);
                break;
            case FAIL:
                log.error("Can not change state to " + nextState);
                break;
        }
    }

    private void runAutoBuy() {
        restoreParameters();
    }

    private State defineNextState() {
        if (shouldSleep())
            return SLEEP;
        if (state == INITIALIZED) {
            if (isAutoBuyEnabled())
                return State.ON_AUTO_BUY;
            else
                return State.ON_NO_ACTIVITY;
        } else if (state == ON_AUTO_BUY && currentStateMinutes >= config.interruptAutoBuyEvery()) {
            return State.ON_NO_ACTIVITY;
        } else if (state == ON_NO_ACTIVITY && isPendingUpdate()) {
            return State.ON_AUTO_BUY;
        }
        if (state == SLEEP && shouldResume()) {
            resumeSystem();
            return State.ON_AUTO_BUY;
        }

        return state;
    }

    private boolean shouldResume() {
        return sleepTime > config.getSleepDurationMinutes();
    }

    private boolean shouldSleep() {
        return noSleepTime >= config.getSleepAfterMinutes();
    }

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

    private void runNoActivity() {
        if (suspended) {
            log.info("[FlowController] can not run NoWork on suspended system");
            return;
        }
        if (!parameterService.getUserParameters().isNoActivityEnabled()) {
            log.info("[FlowController] NoWork was disabled manually");
            return;
        }
        if (!isPendingUpdate()) {
            log.info("[FlowController] No players pending for update");
            return;
        }

        backupCurrentParameters();
        UserParameters parameters = new UserParameters();
        parameters.disableAll();
        parameters.setNoActivityEnabled(true);
        parameterService.updateParameters(ParameterUpdateRequest.fromParameters(parameters));

    }

    private boolean isPendingUpdate() {
        Queue<Long> queue = noActivityService.getPendingQueue();
        if (!parameterService.getUserParameters().isNoActivityEnabled() || queue == null || queue.isEmpty())
            return false;
        return true;
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

    private void init() {
        config = getFlowConfig();
        log.info("[Flow Controller] is started...");
    }

    private FlowConfig getFlowConfig() {
        return new FlowConfig();
    }

    public boolean isAutoBuyEnabled() {
        return parameterService.getUserParameters().isAutoBuyEnabled();
    }

    @Data
    public static class FlowConfig {
        private int sleepAfterMinutes = 60;
        private int sleepDurationMinutes = 20;
        private int noActivityActivateEvery = 20;

        public int interruptAutoBuyEvery() {
            return noActivityActivateEvery;
        }
    }
}

enum State {
    INITIALIZED, SLEEP, ON_AUTO_BUY, ON_NO_ACTIVITY, NEED_TRADEPILE_UPDATE, FAIL
}
