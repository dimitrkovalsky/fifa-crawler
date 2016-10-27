package com.liberty.rest.response;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
@Data
@AllArgsConstructor
public class ConfigResponse {

  private Map<String, Integer> tags;
  private Set<String> activeTags;
}
