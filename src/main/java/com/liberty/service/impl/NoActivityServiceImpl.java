package com.liberty.service.impl;

import com.liberty.common.DelayHelper;
import com.liberty.model.PlayerInfo;
import com.liberty.service.NoActivityService;
import com.liberty.service.PriceService;
import com.liberty.service.RequestService;
import com.liberty.service.TagService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Service
@Slf4j
public class NoActivityServiceImpl implements NoActivityService {

    public static final String[] tags = {"custom", "medium", "cheap"};
    private int currentTagIndex = 0;
    public static final int TO_UPDATE_PLAYERS_AMOUNT = 5;
    public static final int REQUEST_PER_MINUTE = 30;
    private int currentSkip = 0;
    private boolean completed = false;

    private int completedCount = 0;

    @Autowired
    private RequestService requestService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PriceService priceService;

    private Runnable onPriceUpdated;

    @Getter
    private boolean updateInProgress;

    private Queue<Long> pendingUpdate;

    @Override
    public void updatePlayerPrices() {
        if (!CollectionUtils.isEmpty(pendingUpdate)) {
            updatePending();
            return;
        }
        String tag = tags[currentTagIndex];
        if (checkCompleted(tag)) return;

        List<PlayerInfo> players = tagService.getByTag(tag);
        if (currentSkip >= players.size()) {
            completed = true;
            return;
        }

        List<PlayerInfo> toUpdate = players.stream().skip(currentSkip).limit(TO_UPDATE_PLAYERS_AMOUNT)
                .collect(Collectors.toList());
        for (PlayerInfo info : toUpdate) {
            if (info.getProfile() != null) {
                priceService.findMinPrice(info.getProfile().getId());
            }
            currentSkip++;
            if (requestService.getRequestRate() >= REQUEST_PER_MINUTE) {
                break;
            }
            DelayHelper.wait(2000, 100);
        }
        log.info("[NoActivityServiceImpl] Successfully updated players from [ " + tag + "] skip : " + currentSkip
                + ". Rate => " + requestService.getRequestRate());

    }

    private boolean checkCompleted(String tag) {
        if (completed) {
            log.info("Completed price update for tag : " + tag);
            completedCount++;
            if (completedCount >= 15) {
                completed = false;
                completedCount = 0;
                currentSkip = 0;
                currentTagIndex++;
                if (currentTagIndex >= tags.length) {
                    currentTagIndex = 0;
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void updatePending() {
        log.info("Trying to update from pending queue : " + pendingUpdate.size());
        Long playerId = pendingUpdate.poll();
        int iteration = 0;
        while (playerId != null && iteration < TO_UPDATE_PLAYERS_AMOUNT) {
            priceService.findMinPrice(playerId);
            iteration++;
            if (requestService.getRequestRate() >= REQUEST_PER_MINUTE) {
                break;
            }
            DelayHelper.wait(2000, 100);
        }

        log.info("[NoActivityServiceImpl] Successfully updated " + iteration + " players from pendingQueue .Rate = > "
                + requestService.getRequestRate());
        if (pendingUpdate.isEmpty()) {
            updateInProgress = false;
            onPriceUpdated.run();
        }
    }

    @Override
    public void shouldUpdate(List<Long> ids, Runnable onComplete) {
        pendingUpdate = new LinkedList<>();
        updateInProgress = true;
        pendingUpdate.addAll(ids);
        this.onPriceUpdated = onComplete;
    }

    @Override
    public void shouldUpdate(Set<Long> toUpdate) {
        if (CollectionUtils.isEmpty(pendingUpdate)) {
            pendingUpdate = new LinkedList<>();
        }
        Set<Long> set = new HashSet<>();
        set.addAll(toUpdate);
        set.addAll(pendingUpdate);

        pendingUpdate = new LinkedList<>(set);
        updateInProgress = true;
    }


}
