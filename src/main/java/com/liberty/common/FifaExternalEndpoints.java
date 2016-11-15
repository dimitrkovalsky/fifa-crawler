package com.liberty.common;


public interface FifaExternalEndpoints {

    String DOMAIN_EXTERNAL = "https://utas.external.s2.fut.ea.com";
    String GAME_PATH = "/ut/game/fifa17";
    String MAIN_PATH = DOMAIN_EXTERNAL + GAME_PATH;

    String TRADE_LINE_URL = MAIN_PATH + "/tradepile";
    String KEEP_ALIVE_URL = "https://www.easports.com/fifa/api/keepalive";
    String SEARCH_URL = DOMAIN_EXTERNAL +
            GAME_PATH + "/transfermarket?start=%s&num=16&type=player&maskedDefId=%s&maxb=%s";

    String FULL_SEARCH_URL = DOMAIN_EXTERNAL + GAME_PATH + "/transfermarket?%s";
    String INFORM_SEARCH_URL =
            MAIN_PATH + "/transfermarket?" + "rare=SP&num=16&type=player&maxb=%s&maskedDefId=%s&start=0";
    String LOGGED_IN = "https://www.easports.com/fifa/api/isUserLoggedIn";
    String AUTH = "https://www.easports.com/iframe/fut17/p/ut/auth";

    String BID_URL = MAIN_PATH + "/trade/%s/bid";
    String ITEM_URL = MAIN_PATH + "/item";
    String ITEMS_URL = MAIN_PATH + "/purchased/items";
    String AUCTION_HOUSE_URL = "https://utas.external.s2.fut.ea.com/ut/game/fifa17/auctionhouse";

    String STATUS_URL = MAIN_PATH + "/trade/status?tradeIds=%s";
    String REMOVE_SOLD = MAIN_PATH + "/trade/sold";
    String RELIST = MAIN_PATH + "/auctionhouse/relist";
    String GET_UNASSIGNED = MAIN_PATH + "/purchased/items";
    String WATCHLIST_URL = MAIN_PATH + "/watchlist";
    String WATCHLIST_WITH_IDS_URL = MAIN_PATH + "/watchlist?tradeId=%d";
    String MY_PLAYERS = MAIN_PATH + "/club?type=1&level=10&start=%d&count=96";
}
