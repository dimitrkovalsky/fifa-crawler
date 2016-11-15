package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liberty.model.Club;
import com.liberty.model.League;
import com.liberty.model.Nation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Items implements Serializable {

    @JsonProperty("commonName")
    private String commonName;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("headshotImgUrl")
    private String headshotImgUrl;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("league")
    private League league;

    @JsonProperty("nation")
    private Nation nation;

    @JsonProperty("club")
    private Club club;

    @JsonProperty("headshot")
    private Headshot headshot;

    @JsonProperty("specialImages")
    private SpecialImages specialImages;

    @JsonProperty("position")
    private String position;

    @JsonProperty("playStyle")
    private String playStyle;

    @JsonProperty("playStyleId")
    private Long playStyleId;

    @JsonProperty("height")
    private double height;

    @JsonProperty("weight")
    private double weight;

    @JsonProperty("birthdate")
    private String birthdate;

    @JsonProperty("age")
    private double age;

    @JsonProperty("acceleration")
    private double acceleration;

    @JsonProperty("aggression")
    private double aggression;

    @JsonProperty("agility")
    private double agility;

    @JsonProperty("balance")
    private double balance;

    @JsonProperty("ballcontrol")
    private double ballcontrol;

    @JsonProperty("foot")
    private String foot;

    @JsonProperty("skillMoves")
    private double skillMoves;

    @JsonProperty("crossing")
    private double crossing;

    @JsonProperty("curve")
    private double curve;

    @JsonProperty("dribbling")
    private double dribbling;

    @JsonProperty("finishing")
    private double finishing;

    @JsonProperty("freekickaccuracy")
    private double freekickaccuracy;

    @JsonProperty("gkdiving")
    private double gkdiving;

    @JsonProperty("gkhandling")
    private double gkhandling;

    @JsonProperty("gkkicking")
    private double gkkicking;

    @JsonProperty("gkpositioning")
    private double gkpositioning;

    @JsonProperty("gkreflexes")
    private double gkreflexes;

    @JsonProperty("headingaccuracy")
    private double headingaccuracy;

    @JsonProperty("interceptions")
    private double interceptions;

    @JsonProperty("jumping")
    private double jumping;

    @JsonProperty("longpassing")
    private double longpassing;

    @JsonProperty("longshots")
    private double longshots;

    @JsonProperty("marking")
    private double marking;

    @JsonProperty("penalties")
    private double penalties;

    @JsonProperty("positioning")
    private double positioning;

    @JsonProperty("potential")
    private double potential;

    @JsonProperty("reactions")
    private double reactions;

    @JsonProperty("shortpassing")
    private double shortpassing;

    @JsonProperty("shotpower")
    private double shotpower;

    @JsonProperty("slidingtackle")
    private double slidingtackle;

    @JsonProperty("sprintspeed")
    private double sprintspeed;

    @JsonProperty("standingtackle")
    private double standingtackle;

    @JsonProperty("stamina")
    private double stamina;

    @JsonProperty("strength")
    private double strength;

    @JsonProperty("vision")
    private double vision;

    @JsonProperty("volleys")
    private double volleys;

    @JsonProperty("weakFoot")
    private double weakFoot;

    @JsonProperty("traits")
    private List<String> traits;

    @JsonProperty("specialities")
    private Object specialities;

    @JsonProperty("atkWorkRate")
    private String atkWorkRate;

    @JsonProperty("defWorkRate")
    private String defWorkRate;

    @JsonProperty("playerType")
    private String playerType;

    @JsonProperty("attributes")
    private List<Attributes> attributes;

    @JsonProperty("name")
    private String name;

    @JsonProperty("quality")
    private String quality;

    @JsonProperty("color")
    private String color;

    @JsonProperty("isGK")
    private boolean isGK;

    @JsonProperty("positionFull")
    private String positionFull;

    @JsonProperty("isSpecialType")
    private boolean isSpecialType;

    @JsonProperty("contracts")
    private Object contracts;

    @JsonProperty("fitness")
    private Object fitness;

    @JsonProperty("rawAttributeChemistryBonus")
    private Object rawAttributeChemistryBonus;

    @JsonProperty("isLoan")
    private IsLoan isLoan;

    @JsonProperty("squadPosition")
    private Object squadPosition;

    @JsonProperty("itemType")
    private String itemType;

    @JsonProperty("discardValue")
    private Object discardValue;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("baseId")
    private Long baseId;

    @JsonProperty("rating")
    private int rating;


}