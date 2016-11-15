package com.liberty.common;

import com.liberty.model.market.AuctionInfo;

import java.util.Comparator;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.06.2016.
 */
public class Comparators {

    public static Comparator<AuctionInfo> getAuctionInfoComparator() {
        return (a1, a2) -> {
            int contract1 = a1.getItemData().getContract();
            int contract2 = a2.getItemData().getContract();
            if (contract1 <= 0 && contract2 <= 0) {
                return 0;
            }
            if (contract1 <= 0 && contract2 > 0) {
                return 1;
            }
            if (contract1 > 0 && contract2 <= 0) {
                return -1;
            }
            return a1.getBuyNowPrice().compareTo(a2.getBuyNowPrice());
        };
    }
}
