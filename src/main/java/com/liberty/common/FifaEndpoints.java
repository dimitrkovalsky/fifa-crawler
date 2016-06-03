package com.liberty.common;


public interface FifaEndpoints {
  String TRADE_LINE_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/tradepile";
  String KEEP_ALIVE_URL = "https://www.easports.com/fifa/api/keepalive";
  String SEARCH_URL = "https://utas.s2.fut.ea" +
      ".com/ut/game/fifa16/transfermarket?num=16&type=player&maxb=%s&maskedDefId=%s&start=0";
  String LOGGED_IN = "https://www.easports.com/fifa/api/isUserLoggedIn";
}
