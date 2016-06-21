package com.liberty.common;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.FifaEndpoints.KEEP_ALIVE_URL;
import static com.liberty.common.RequestHelper.readResult;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.06.2016.
 */
@Slf4j
abstract class BaseFifaRequests {

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
    request.setHeader("X-XSRF-TOKEN", getXsrfToken());
    request.setHeader(HttpHeaders.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:46.0) Gecko/20100101 Firefox/46.0");
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

  protected Optional<String> execute(HttpRequestBase request) {
    String result = null;
    try {
      ContentEncodingHttpClient client = new ContentEncodingHttpClient();
      client.setRedirectStrategy(new DefaultRedirectStrategy() {
        public boolean isRedirected(HttpRequest request, HttpResponse response,
                                    HttpContext context) {
          boolean isRedirect = false;
          try {
            isRedirect = super.isRedirected(request, response, context);
          } catch (ProtocolException e) {
            log.error(e.getMessage());
          }
          if (!isRedirect) {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 301 || responseCode == 302) {
              return true;
            }
          }
          return isRedirect;
        }
      });
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


  protected HttpPost createBidRequest(String url) {
    HttpPost request = new HttpPost(url);
    setBaseHeaders(request);
    request.setHeader("X-HTTP-Method-Override", "PUT");
    request.setHeader("X-UT-PHISHING-TOKEN", getPhishingToken());
    request.setHeader("X-UT-SID", getSessionId());
    return request;
  }

  protected HttpPost createDeleteRequest(String url) {
    return createRequest(url, "DELETE");
  }

  protected HttpPost createPutRequest(String url) {
    return createRequest(url, "PUT");
  }

  protected HttpPost createRequest(String url, String method) {
    HttpPost request = new HttpPost(url);
    setBaseHeaders(request);

    request.setHeader("X-HTTP-Method-Override", method);
    request.setHeader("X-UT-PHISHING-TOKEN", getPhishingToken());
    request.setHeader("X-UT-SID", getSessionId());
    return request;
  }

  private void setBaseHeaders(HttpPost request) {
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "Accept-Language");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader(HttpHeaders.HOST, "utas.s2.fut.ea.com");
    request.setHeader(HttpHeaders.REFERER,
        "https://www.easports.com/iframe/fut16/bundles/futweb/web/flash/FifaUltimateTeam.swf?cl=159444");
    request.setHeader(HttpHeaders.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36 OPR/37.0.2178.43");
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader("X-Requested-With", "ShockwaveFlash/22.0.0.192");
    request.setHeader("X-UT-Embed-Error", "true");
  }

  protected HttpPost createRequest(String url) {
    return createRequest(url, "GET");
  }

  protected HttpPost createAuthRequest(String url) {
    HttpPost request = new HttpPost(url);
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader(HttpHeaders.HOST, "www.easports.com");
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader(HttpHeaders.REFERER,
        "https://www.easports.com/iframe/fut16/bundles/futweb/web/flash/FifaUltimateTeam.swf?cl=159444");
    request.setHeader(HttpHeaders.USER_AGENT,
        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36 OPR/37.0.2178.43");
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader("X-Requested-With", "ShockwaveFlash/22.0.0.192");
    request.setHeader("X-UT-Embed-Error", "true");
    request.setHeader("X-HTTP-Method-Override", "POST");
//    request.setHeader("Easw-Session-Data-Nucleus-Id", getNucleusId());
    request.setHeader("X-UT-Route", "https://utas.s2.fut.ea.com:443");
    request.setHeader("X-UT-PHISHING-TOKEN", getPhishingToken());

    return request;
  }

  protected abstract String getSessionId();

  protected abstract String getPhishingToken();

  public String getNucleusId() {
    return "2311254984";
  }

}
