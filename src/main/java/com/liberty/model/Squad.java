package com.liberty.model;

import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.11.2016.
 */
@Data
public class Squad {

  private Long squadId;
  private Integer minPrice;
  private List<SquadPlayer> players;
}
