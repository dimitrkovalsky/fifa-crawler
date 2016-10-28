package com.liberty.model;

import com.liberty.processors.pojo.Attributes;
import com.liberty.processors.pojo.Items;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
@Data
@Document(collection = "player_profile")
public class PlayerProfile {

  @Id
  private Long id;

  public String commonName;

  public String firstName;

  public String headshotImgUrl;

  public String lastName;

  public Long leagueId;

  public Long nationId;

  public Long clubId;

  public String position;

  public String playerType;

  public List<Attributes> attributes;

  public String name;

  public String quality;

  public String color;

  public boolean isGK;

  public boolean isSpecialType;

  public Long baseId;

  public Integer rating;

  public Items fullInfo;

  private Date updated = new Date();
}
