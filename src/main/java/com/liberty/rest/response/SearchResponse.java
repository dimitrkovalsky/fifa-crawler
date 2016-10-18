package com.liberty.rest.response;

import com.liberty.model.TradeInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.10.2016.
 */
@Data
@AllArgsConstructor
public class SearchResponse {
  private List<TradeInfo> items;
}
