package com.liberty.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Data
public class FifaPlayerSuggestion {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("c")
  private String title;

  @JsonProperty("f")
  private String firstName;

  @JsonProperty("l")
  private String lastName;

  @JsonProperty("r")
  private Integer rating;

  @JsonProperty("n")
  private Integer nationId;
}
