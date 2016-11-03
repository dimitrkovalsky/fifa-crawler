package com.liberty.service;

import com.liberty.model.Squad;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
public interface SquadBuilderService {

  Squad fetchPricesForSquad(Long squadId);
}
