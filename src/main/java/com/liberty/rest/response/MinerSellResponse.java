package com.liberty.rest.response;

import com.liberty.service.strategy.AutomaticSellStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: Dimitr
 * Date: 27.11.2016
 * Time: 11:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinerSellResponse {
    private AutomaticSellStrategy.MinerBid bid;
    private boolean priceDistributionValid;
}
