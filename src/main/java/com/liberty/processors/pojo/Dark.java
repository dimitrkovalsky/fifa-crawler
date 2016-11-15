package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class Dark implements Serializable {

    @JsonProperty("small")
    public String small;

    @JsonProperty("medium")
    public String medium;

    @JsonProperty("large")
    public String large;


}