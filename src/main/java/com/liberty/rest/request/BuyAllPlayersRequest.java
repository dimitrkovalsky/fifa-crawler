package com.liberty.rest.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitr
 * Date: 06.11.2016
 * Time: 10:57
 */
@Data
public class BuyAllPlayersRequest {

    private List<BuySinglePlayerRequest> players = new ArrayList<>();

}
