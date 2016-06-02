package com.liberty.common;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.RequestHelper.readResult;

/**
 * User: Dimitr Date: 02.06.2016 Time: 7:34
 */
@Slf4j
public class FifaRequests {

  private final static String TRADE_LINE_URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/tradepile";
  private final static String KEEP_ALIVE_URL = "https://www.easports.com/fifa/api/keepalive";
  private final String SEARCH_URL = "https://utas.s2.fut.ea" +
      ".com/ut/game/fifa16/transfermarket?num=16&type=player&maxb=3000&maskedDefId=201510&start=0";

  public void getTradeLine() {
    HttpPost request = createRequest(TRADE_LINE_URL);
    Optional<String> result = execute(request);
    System.out.println(result);
  }

  public void keepAlive() throws IOException {
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(KEEP_ALIVE_URL);

    request.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "Accept-Language");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    request.setHeader(HttpHeaders.HOST, "www.easports.com");
    request.setHeader(HttpHeaders.REFERER, "https://www.easports.com/fifa/ultimate-team/web-app");
    request.setHeader("X-Requested-With", "XMLHttpRequest");
    request.setHeader("X-XSRF-TOKEN",getXsrfToken());
    request.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
    request.setHeader("Cookie", "hl=us; 7adb0816=1; " +
        "EASSSO=cc744a530b2255d4cd768773e499d2be64983abd6926a00bd49a5c36cf8a1b2b; utag_main=v_id:01550814248d001e4881f541f18e0a049003300d009dc$_sn:2$_ss:1$_st:1464844167977$_pn:1%3Bexp-session$ses_id:1464842367977%3Bexp-session; optimizelyEndUserId=oeu1464719386422r0.2961764697498508; optimizelySegments=%7B%22172174479%22%3A%22none%22%2C%22172202804%22%3A%22direct%22%2C%22172207507%22%3A%22false%22%2C%22172316047%22%3A%22ff%22%2C%22265568016%22%3A%22true%22%7D; optimizelyBuckets=%7B%7D; __utma=242180630.368449729.1464842370.1464842370.1464842370.1; __utmz=242180630.1464842370.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _ceg.s=o84nlt; _ceg.u=o84nlt; FUTWebPhishing1214832867=9002495091363405202; EASFC-WEB-SESSION=adf70u30gaa37k6p3eik565ub4; XSRF-TOKEN=Xek4krbtHItMDuCvvweV9qs0a9XE6hDoOTNEk54j1IE; __utmb=242180630.12.10.1464842370; __utmc=242180630; __utmt_~1=1");

    HttpResponse response = client.execute(request);
    if (response.getStatusLine().getStatusCode() == 200) {
      InputStream content = response.getEntity().getContent();
      String result = readResult(content);
      System.out.println(result);
    } else {
      log.error("HTTP STATUS for : " + request.getURI() + " ==>>> " + response.getStatusLine()
          .getStatusCode() + ". " + response.getStatusLine().getReasonPhrase());

    }
  }

  private String getXsrfToken() {
    return "Xek4krbtHItMDuCvvweV9qs0a9XE6hDoOTNEk54j1IE";
  }

  public void searchPlayer() {
    HttpPost request = createRequest(SEARCH_URL);
    Optional<String> execute = execute(request);
    System.out.println(execute);
  }

  public static void main(String[] args) throws IOException {
    FifaRequests fifaRequests = new FifaRequests();
    fifaRequests.keepAlive();
  }

  private Optional<String> execute(HttpPost request) {
    String result = null;
    try {
      HttpClient client = new ContentEncodingHttpClient();
      HttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() == 200) {
        InputStream content = response.getEntity().getContent();
        result = readResult(content);
      } else {
        log.error("HTTP STATUS for : " + request.getURI() + " ==>>> " + response.getStatusLine()
            .getStatusCode() + ". " + response.getStatusLine().getReasonPhrase());

      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return Optional.ofNullable(result);
  }

  private HttpPost createRequest(String url) {
    HttpPost request = new HttpPost(url);
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "Accept-Language");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader(HttpHeaders.HOST, "utas.s2.fut.ea.com");
    request.setHeader(HttpHeaders.REFERER,
        "https://www.easports.com/iframe/fut16/bundles/futweb/web/flash/FifaUltimateTeam.swf?cl=158890");
    request.setHeader(HttpHeaders.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36 OPR/37.0.2178.43");
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader("X-HTTP-Method-Override", "GET");
    request.setHeader("X-Requested-With", "ShockwaveFlash/21.0.0.242");
    request.setHeader("X-UT-Embed-Error", "true");
    request.setHeader("X-UT-PHISHING-TOKEN", getPhishingToken());
    request.setHeader("X-UT-SID", getSessionId());
    return request;
  }

  public void execute2() throws IOException {
    HttpPost request = createRequest(SEARCH_URL);
    Optional<String> execute = execute(request);
    System.out.println(execute);
  }

  private String getSessionId() {
    return "aeeac1b4-824e-4e47-a9a2-83e20c13d7e1";
  }

  public String getPhishingToken() {
    return "9002495091363405202";
  }
}
