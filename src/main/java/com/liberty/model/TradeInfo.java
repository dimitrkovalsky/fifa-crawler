package com.liberty.model;

import com.liberty.model.market.AuctionInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.10.2016.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeInfo {
    private AuctionInfo auctionInfo;
    private PlayerTradeStatus tradeStatus;
    private PlayerProfile profile;
}
