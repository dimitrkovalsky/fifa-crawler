package com.liberty.common;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.FifaEndpoints.*;

/**
 * User: Dimitr Date: 19.07.2016 Time: 9:19
 */
@Slf4j
public class UrlResolver {

    public static boolean externalUrl = true;

    public static void setExternalUrl(boolean externalUrl) {
        if (UrlResolver.externalUrl != externalUrl) {
            log.info("Changing source externalUrl = " + externalUrl);
            UrlResolver.externalUrl = externalUrl;
        }
    }

    public static String getItemUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.ITEM_URL;
        }
        return ITEM_URL;
    }

    public static String getAuctionHouseUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.AUCTION_HOUSE_URL;
        }
        return AUCTION_HOUSE_URL;
    }

    public static String getRemoveSold() {
        if (externalUrl) {
            return FifaExternalEndpoints.REMOVE_SOLD;
        }
        return FifaEndpoints.REMOVE_SOLD;
    }

    public static String getRelistUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.RELIST;
        }
        return FifaEndpoints.RELIST;
    }

    public static String getGetUnassignedUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.GET_UNASSIGNED;
        }
        return FifaEndpoints.GET_UNASSIGNED;
    }

    public static String getStatusUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.STATUS_URL;
        }
        return STATUS_URL;
    }

    public static String getBidUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.BID_URL;
        }
        return BID_URL;
    }

    public static String getTradeLineUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.TRADE_LINE_URL;
        }
        return TRADE_LINE_URL;
    }

    public static String getWatchlistUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.WATCHLIST_URL;
        }
        return WATCHLIST_URL;
    }

    public static String getSearchUrl() {
        if (externalUrl) {
            return FifaExternalEndpoints.SEARCH_URL;
        }
        return SEARCH_URL;
    }
}
