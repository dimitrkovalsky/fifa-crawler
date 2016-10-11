package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;


public class Attributes implements Serializable{

    @JsonProperty("name")
    public String name;

    @JsonProperty("value")
    public double value;

    @JsonProperty("chemistryBonus")
    public List<Object> chemistryBonus;


}