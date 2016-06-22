package com.liberty.model.market;

import java.util.List;

import lombok.Data;

@Data
public class ItemData {

  private Long id;
  private Long timestamp;
  private String formation;
  private boolean untradeable;
  private Long assetId;
  private int rating;
  private String itemType;
  private int resourceId;
  private int owners;
  private int discardValue;
  private String itemState;
  private int cardsubtypeid;
  private int lastSalePrice;
  private int morale;
  private int fitness;
  private String injuryType;
  private int injuryGames;
  private String preferredPosition;
  private List<StatsList> statsList;
  private List<LifetimeStat> lifetimeStats;
  private int training;
  private int contract;
  private int suspension;
  private List<AttributeList> attributeList;
  private int teamid;
  private int rareflag;
  private int playStyle;
  private int leagueId;
  private int assists;
  private int lifetimeAssists;
  private int loyaltyBonus;
  private int pile;
  private int nation;
}
