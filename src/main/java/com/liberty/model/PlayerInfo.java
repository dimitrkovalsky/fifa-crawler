package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
  private PlayerStats stats;

  private Price price;



}
