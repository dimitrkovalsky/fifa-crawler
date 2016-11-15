package com.liberty.rest.response;

import com.liberty.model.TradeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.10.2016.
 */
@Data
@AllArgsConstructor
public class SearchResponse {
    private List<TradeInfo> items;
}
