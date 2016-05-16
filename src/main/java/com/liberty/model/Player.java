package com.liberty.model;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
public class Player {

  private long id;
  private String name;
  private String image;
  private int total;
  private int pace;
  private int shooting;
  private int passing;
  private int dribbling;
  private int defending;
  private int heading;
  private int league;
  private String leagueName;
  private String teamName;
  private String position;
  private String url;

  private Price price;

  private byte skillMoves;
  private byte weakFoot;

}
