package com.liberty.processors.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;


public class FifaResponse implements Serializable {

    @JsonProperty("page")
    public double page;

    @JsonProperty("totalPages")
    public double totalPages;

    @JsonProperty("totalResults")
    public double totalResults;

    @JsonProperty("type")
    public String type;

    @JsonProperty("count")
    public double count;

    @JsonProperty("items")
    public List<Items> items;


}