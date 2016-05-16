package com.liberty.model;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class PlayerInfo {

  private long id;
  private String name;
  private String image;

  private String leagueName;
  private String teamName;
  private String position;
  private String url;
  private PlayerStats stats;

  private Price price;



}
