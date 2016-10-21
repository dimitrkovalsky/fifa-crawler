package com.liberty.model.market;

import java.util.List;

import lombok.Data;

@Data
public class ItemData {

  private Long id;
  private Long timestamp;
  private String formation;
  private Boolean untradeable;
  private Long assetId;
  private Integer rating;
  private String itemType;
  private Integer resourceId;
  private Integer owners;
  private Integer discardValue;
  private String itemState;
  private Integer cardsubtypeid;
  private Integer lastSalePrice;
  private Integer morale;
  private Integer fitness;
  private String injuryType;
  private Integer injuryGames;
  private String preferredPosition;
  private List<StatsList> statsList;
  private List<LifetimeStat> lifetimeStats;
  private Integer training;
  private Integer contract;
  private Integer suspension;
  private List<AttributeList> attributeList;
  private Integer teamid;
  private Integer rareflag;
  private Integer playStyle;
  private Integer leagueId;
  private Integer assists;
  private Integer lifetimeAssists;
  private Integer loyaltyBonus;
  private Integer pile;
  private Integer nation;
  private String firstName;
  private String lastName;
  private String negotiation;
  private Integer marketDataMinPrice;
  private Integer marketDataMaxPrice;
  private Integer cardassetid;
  private Object value;
  private String category;
  private String manufacturer;
  private String name;
  private String description;
  private String biodescription;
  private Long stadiumid;
  private Long capacity;
  private Object trainingItem;


  private boolean fromTargets = false;
  // used to sell item from transfer target
  private Long tradeId;

}
