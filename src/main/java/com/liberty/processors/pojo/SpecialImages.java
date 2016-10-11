package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


public class SpecialImages implements Serializable{

    @JsonProperty("largeTOTWImgUrl")
    public String largeTOTWImgUrl;

    @JsonProperty("medTOTWImgUrl")
    public String medTOTWImgUrl;


}