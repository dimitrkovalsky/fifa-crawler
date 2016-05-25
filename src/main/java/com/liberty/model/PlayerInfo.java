package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
@Document(collection = "player_info")
public class PlayerInfo {

  @Id
  private Long id;
  private String name;
  private String image;

  private String leagueName;
  private String teamName;
  private String position;
  private String url;
  private String playCardPicture;
  private String nation;
  private String source;
  private PlayerStats stats;
  private String cardType;

  private Price price;



}
