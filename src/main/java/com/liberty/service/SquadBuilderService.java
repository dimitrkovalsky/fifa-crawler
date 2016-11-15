package com.liberty.service;

import com.liberty.model.FullSquad;
import com.liberty.rest.request.BuyAllPlayersRequest;
import com.liberty.rest.request.BuySinglePlayerRequest;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
public interface SquadBuilderService {

    FullSquad fetchPricesForSquad(Long squadId);

    FullSquad updateSquad(Long squadId);

    void buySquad(Long squadId);

    boolean buyPlayer(BuySinglePlayerRequest request);

    void buyAllPlayers(BuyAllPlayersRequest request);
}
