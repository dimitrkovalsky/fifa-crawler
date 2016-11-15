package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class Headshot implements Serializable {

    @JsonProperty("largeImgUrl")
    public String largeImgUrl;

    @JsonProperty("medImgUrl")
    public String medImgUrl;

    @JsonProperty("smallImgUrl")
    public String smallImgUrl;


}