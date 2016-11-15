package com.liberty.model.market;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 10.10.2016.
 */
@Data
public class Watchlist {

    private List<AuctionInfo> auctionInfo = new ArrayList<>();
    private List<Object> duplicateItemIdList;
    private Integer credits;
    private Integer total;
}
