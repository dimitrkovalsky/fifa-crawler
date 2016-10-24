package com.liberty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
public class Main2 {

  public static void main(String[] args) {
    String myCookie =
        "optimizelyEndUserId=oeu1450945658852r0.39485638681799173;__utma=103303007.2105186675.1448875174.1448875174.1451291418.2;_nx_mpcid=9ddb98a4-ab3e-4c9a-a223-6764be03113f;utag_main=_st:1464791605501$v_id:0151d319be6300099ce81dcd82090c07e006e07600bd0$_sn:4$_ss:0$_pn:2%3Bexp-session$ses_id:1464789794804%3Bexp-session;optimizelySegments=%7B%222200840229%22%3A%22opera%22%2C%222207560119%22%3A%22false%22%2C%222209790291%22%3A%22none%22%2C%222215600082%22%3A%22search%22%7D;optimizelyBuckets=%7B%7D;_ga=GA1.2.2105186675.1448875174";
    String appCookies =
        "futweb=86c3ashbc5gch75hsimq1qssi0; optimizelyEndUserId=oeu1448875029235r0.6976072182878852; _ceg.s=oemlej; _ceg.u=oemlej; hl=us; XSRF-TOKEN=nxftpb1Jj-W8LPych8wJ9slKy6tjJgGfxcl3cYI8d94; DOT_COM_PHPSESSID=54n17g2ie5bm9f91igqn8337u7; EASFC-WEB-SESSION=ne24kp9omrelqam17jk76jfs20; utag_main=v_id:015157ae6d000001b6614c0e093b0c07f003407700bd0$_sn:698$_ss:0$_st:1477303210999$_pn:5%3Bexp-session$ses_id:1477301060682%3Bexp-session; a64994dc=1; EASSSO=f3d652bfe8a71806c5021d17ec59356b79f63096339e5f370eba657fcb06ac43; _ga=GA1.2.815150707.1448985912; optimizelySegments=%7B%22172174479%22%3A%22fifa17_hd_ww_ic_ic_t%22%2C%22172202804%22%3A%22search%22%2C%22172207507%22%3A%22false%22%2C%22172316047%22%3A%22opera%22%2C%22265568016%22%3A%22true%22%7D; optimizelyBuckets=%7B%7D; FUTWebPhishing1214832867=1056475053229753232; __utmt_~1=1; __utma=242180630.1488750923.1476966783.1477301418.1477301418.7; __utmb=242180630.4.10.1477301418; __utmc=242180630; __utmz=242180630.1465543127.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)";

    Map<String, String> myMap = new HashMap<>();
    Map<String, String> appMap = new HashMap<>();
    split(myCookie, myMap);
    split(appCookies, appMap);
    Set<String> myKeys = myMap.keySet();
    Set<String> appKeys = appMap.keySet();

    List<String> diff = new ArrayList<>();

    appMap.forEach((k, v) -> {
      if (!myKeys.contains(k)) {
        System.out.println("Diff : " + k + " \t ====== " + v);
      }
    });

    myMap.forEach((k, v) -> {
      if (!appKeys.contains(k)) {
        System.out.println("Remove =========>>>>> " + k + " \t ====== " + v);
      }
    });
  }

  private static void split(String myCookie, Map<String, String> myMap) {
    Arrays.stream(myCookie.split(";")).forEach(x -> {
      String[] split = x.split("=");
      myMap.put(split[0], split[1]);
    });
  }
}
