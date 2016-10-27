package com.liberty.service;

import com.liberty.model.PlayerTradeStatus;
import com.liberty.model.TradeInfo;
import com.liberty.rest.request.MarketSearchRequest;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 27.10.2016.
 */
public interface SearchService {

  List<TradeInfo> search(MarketSearchRequest searchRequest);

  List<PlayerTradeStatus> search(String phrase);
}
