package com.liberty.model;

import com.liberty.service.impl.HistoryServiceImpl;

import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Data
public class FullSquad {

  private Long squadId;
  private HistoryServiceImpl.HistoryPoint price;
  private List<SquadPlayer> players;
  private String date;
}
