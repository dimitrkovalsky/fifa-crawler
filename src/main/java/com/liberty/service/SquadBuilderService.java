package com.liberty.service;

import com.liberty.model.FullSquad;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
public interface SquadBuilderService {

  FullSquad fetchPricesForSquad(Long squadId);

  FullSquad updateSquad(Long squadId);

  void buySquad(Long squadId);
}
