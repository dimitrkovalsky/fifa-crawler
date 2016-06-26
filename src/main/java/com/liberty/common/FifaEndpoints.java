package com.liberty.common;


public interface FifaEndpoints {

  String TRADE_LINE_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/tradepile";
  String KEEP_ALIVE_URL = "https://www.easports.com/fifa/api/keepalive";
  String SEARCH_URL = "https://utas.s2.fut.ea" +
      ".com/ut/game/fifa16/transfermarket?start=%s&num=16&type=player&maskedDefId=%s&maxb=%s";
  String INFORM_SEARCH_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/transfermarket?" +
      "rare=SP&num=16&type=player&maxb=%s&maskedDefId=%s&start=0";
  String LOGGED_IN = "https://www.easports.com/fifa/api/isUserLoggedIn";
  String AUTH = "https://www.easports.com/iframe/fut16/p/ut/auth";
  String BID_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/trade/%s/bid";
  String ITEM_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/item";
  String ITEMS_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/purchased/items";
  String AUCTION_HOUSE_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/auctionhouse";
  String STATUS_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/trade/status?tradeIds=%s";
  String REMOVE_SOLD = "https://utas.s2.fut.ea.com/ut/game/fifa16/trade/sold";
  String RELIST = "https://utas.s2.fut.ea.com/ut/game/fifa16/auctionhouse/relist";
  String GET_UNASSIGNED = "https://utas.s2.fut.ea.com/ut/game/fifa16/purchased/items";
}
