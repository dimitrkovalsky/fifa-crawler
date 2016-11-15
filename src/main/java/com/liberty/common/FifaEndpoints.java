package com.liberty.common;


public interface FifaEndpoints {

    String DOMAIN = "https://utas.s2.fut.ea.com";
    String DOMAIN_EXTERNAL = "https://utas.external.s2.fut.ea.com";
    String TRADE_LINE_URL = DOMAIN + "/ut/game/fifa17/tradepile";
    String KEEP_ALIVE_URL = "https://www.easports.com/fifa/api/keepalive";
    String SEARCH_URL = "https://utas.s2.fut.ea" +
            ".com/ut/game/fifa17/transfermarket?start=%s&num=16&type=player&maskedDefId=%s&maxb=%s";
    String INFORM_SEARCH_URL = DOMAIN + "/ut/game/fifa17/transfermarket?" +
            "rare=SP&num=16&type=player&maxb=%s&maskedDefId=%s&start=0";
    String LOGGED_IN = "https://www.easports.com/fifa/api/isUserLoggedIn";
    String AUTH = "https://www.easports.com/iframe/fut17/p/ut/auth";
    String BID_URL = DOMAIN + "/ut/game/fifa17/trade/%s/bid";
    String ITEM_URL = DOMAIN + "/ut/game/fifa17/item";
    String ITEMS_URL = DOMAIN + "/ut/game/fifa17/purchased/items";
    String AUCTION_HOUSE_URL = DOMAIN + "/ut/game/fifa17/auctionhouse";
    String STATUS_URL = DOMAIN + "/ut/game/fifa17/trade/status?tradeIds=%s";
    String REMOVE_SOLD = DOMAIN + "/ut/game/fifa17/trade/sold";
    String RELIST = DOMAIN + "/ut/game/fifa17/auctionhouse/relist";
    String GET_UNASSIGNED = DOMAIN + "/ut/game/fifa17/purchased/items";
    String WATCHLIST_URL = DOMAIN_EXTERNAL + "/ut/game/fifa17/watchlist";

}
