package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */

@Data
@Document(collection = "config")
public class MarketConfig {

  @Id
  private Integer id;

  private Set<String> activeTags = new HashSet<>();
}
