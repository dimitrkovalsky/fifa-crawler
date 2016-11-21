package com.liberty.service;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 26.10.2016.
 */
public interface NoActivityService {

    Queue<Long> getPendingQueue();

    void updatePlayerPrices();

    void shouldUpdate(List<Long> ids, Runnable onComplete);


    boolean isUpdateInProgress();

    void shouldUpdate(Set<Long> toUpdate);
}
