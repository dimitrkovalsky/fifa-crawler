package com.liberty.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Set;

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
