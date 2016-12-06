package com.liberty.controllers;

import com.liberty.model.UserParameters;
import com.liberty.rest.request.ParameterUpdateRequest;
import com.liberty.service.NoActivityService;
import com.liberty.service.TradeService;
import com.liberty.service.UserParameterService;
import com.liberty.websockets.BuyMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Queue;

import static com.liberty.controllers.State.*;

/**
 * @author Dmytro_Kovalskyi.
 * @since 21.11.2016.
 */
@Component
@Slf4j
public class FlowController implements InitializingBean {

    public static final int TRADEPILE_UPDATE = 10;
    public static final int DEFAULT_PURCHASES = 10;
    public static final int PENDING_QUEUE_SIZE_THRESHOLD = 20;
    private Optional<UserParameters> lastParameters = Optional.empty();

    @Autowired
    private UserParameterService parameterService;

    @Autowired
    private NoActivityService noActivityService;

    @Autowired
    private TradeService tradeService;

    private volatile boolean suspended;
    private FlowConfig config;
    private State state = INITIALIZED;
    private volatile int currentStateMinutes = 0;
    private volatile int noSleepTime = 0;
    private volatile int workingTime = 0;
    private volatile int sleepTime = 0;
    private State previousState;

    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    private void onSchedule() {
        currentStateMinutes++;
        workingTime++;
        checkTradepile();
        if (state != SLEEP)
            noSleepTime++;
        else
            sleepTime++;
        State nextState = defineNextState();
        if (nextState != state) {
            if (changeState(nextState)) {
                currentStateMinutes = 0;
                previousState = state;
                state = nextState;
                log.info("[FlowController] current state : " + state);
            } else {
                log.info("[FlowController] can not change state to : " + nextState);
            }
        }
    }

    private void checkTradepile() {
        if (workingTime % TRADEPILE_UPDATE == 0) {
            BuyMessage tradepileInfo = tradeService.getTradepileInfo();
            Integer canSell = tradepileInfo.getCanSell();
            Integer purchasesRemained = tradepileInfo.getPurchasesRemained();
            int delta = purchasesRemained - canSell;
            if (purchasesRemained <= 0 && delta <= 0) {
                log.info("[FlowController] Trying to update TradePile size");
                int nextPurchases = -delta + DEFAULT_PURCHASES;
                tradeService.updatePurchaseRemained(nextPurchases);
                log.info("[FlowController] Updated TradePile size to " + nextPurchases);
            }
        }
    }

    private boolean changeState(State nextState) {
        log.info("[FlowController] trying to change state from " + state + " to " + nextState);
        switch (nextState) {
            case INITIALIZED:
                log.error("Can not change state to " + nextState);
                return false;
            case SLEEP:
                suspendSystem();
                noSleepTime = 0;
                return true;
            case ON_AUTO_BUY:
                runAutoBuy();
                return true;
            case ON_NO_ACTIVITY:
                return runNoActivity();
            case NEED_TRADEPILE_UPDATE:
                log.error("Can not change state to " + nextState);
                return false;
            case FAIL:
                log.error("Can not change state to " + nextState);
                return false;
        }
        return false;
    }

    private void runAutoBuy() {
        restoreParameters();
    }

    private State defineNextState() {
        if (state == SLEEP && shouldResume()) {
            resumeSystem();
            return previousState;
        }
        if (shouldSleep())
            return SLEEP;
        if (state == INITIALIZED) {
            return getStartState();
        } else if (enableNoActivity()) {
            return State.ON_NO_ACTIVITY;
        } else if (state == ON_NO_ACTIVITY && !isPendingUpdate()) {
            return State.ON_AUTO_BUY;
        }
        if (state == ON_NO_ACTIVITY && tradeService.getMarketInfo().getAutoBuyEnabled()) {
            disableAutoBuy();
            log.info("[FlowController] Auto Buy disabled");
        }

        return state;
    }

    private State getStartState() {
        if (!isPendingUpdate())
            return State.ON_AUTO_BUY;
        else
            return State.ON_NO_ACTIVITY;
    }

    private void disableAutoBuy() {
        UserParameters userParameters = parameterService.getUserParameters();
        userParameters.setAutoBuyEnabled(false);
        parameterService.updateParameters(ParameterUpdateRequest.fromParameters(userParameters));
    }

    private boolean enableNoActivity() {
        if (state == ON_AUTO_BUY && currentStateMinutes >= config.interruptAutoBuyEvery())
            return true;
        Queue<Long> pendingQueue = noActivityService.getPendingQueue();
        if (state == ON_AUTO_BUY && isPendingUpdate() && !CollectionUtils.isEmpty(pendingQueue) &&
                pendingQueue.size() >= PENDING_QUEUE_SIZE_THRESHOLD) {
            log.info("Trying to enable no activity. Pending queue size = " + pendingQueue.size());
            return true;
        }
        return false;
    }

    private boolean shouldResume() {
        return sleepTime > config.getSleepDurationMinutes();
    }

    private boolean shouldSleep() {
        return noSleepTime >= config.getSleepAfterMinutes();
    }

    @PreDestroy
    private void restoreParameters() {
        lastParameters.ifPresent(x -> parameterService.updateParameters(ParameterUpdateRequest.fromParameters(x)));
        lastParameters = Optional.empty();
    }

    private void backupCurrentParameters() {
        UserParameters userParameters = parameterService.getUserParameters();
        lastParameters = Optional.of(userParameters);
    }

    private boolean runNoActivity() {
        if (suspended) {
            log.info("[FlowController] can not run NoWork on suspended system");
            return false;
        }
        if (!parameterService.getUserParameters().isNoActivityEnabled()) {
            log.info("[FlowController] NoWork was disabled manually");
            return false;
        }
//        if (!isPendingUpdate()) {
//            log.info("[FlowController] No players pending for update");
//            return false;
//        }

        backupCurrentParameters();
        UserParameters parameters = parameterService.getUserParameters();
        parameters.setAutoBuyEnabled(false);
        parameters.setRobotEnabled(false);
        parameters.setNoActivityEnabled(true);
        parameterService.updateParameters(ParameterUpdateRequest.fromParameters(parameters));
        return true;

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
        sleepTime = 0;
        workingTime = 0;
        log.info("[FlowController] system flow resumed...");
    }

    private void suspendSystem() {
        if (suspended)
            return;
        log.info("[FlowController] trying to suspend system flow...");
        if (state == ON_NO_ACTIVITY) {
            lastParameters.ifPresent(x -> parameterService.updateParameters(ParameterUpdateRequest.fromParameters(x)));
            lastParameters = Optional.empty();
        }
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
        private int sleepDurationMinutes = 10;
        private int noActivityActivateEvery = 15;

        public int interruptAutoBuyEvery() {
            return noActivityActivateEvery;
        }
    }
}

enum State {
    INITIALIZED, SLEEP, ON_AUTO_BUY, ON_NO_ACTIVITY, NEED_TRADEPILE_UPDATE, FAIL
}
