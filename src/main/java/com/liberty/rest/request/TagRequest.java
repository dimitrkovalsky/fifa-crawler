package com.liberty.rest.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * User: Dimitr
 * Date: 23.10.2016
 * Time: 10:38
 */
@Data
public class TagRequest {

    @NotNull
    private Long playerId;

    @NotNull
    private String tag;
}
