package com.liberty.common;

import com.liberty.rest.request.TokenUpdateRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.RequestHelper.readResult;

/**
 * @author Dmytro_Kovalskyi.
 * @since 03.06.2016.
 */
@Slf4j
abstract class BaseFifaRequests {

  static final long NUCLEUS_PERSONA_ID = 228045231L;
  protected volatile String sessionId = null;
  protected volatile String phishingToken = null;

  public static final String REFERER =
      "https://www.easports.com/iframe/fut17/bundles/futweb/web/flash/FifaUltimateTeam.swf?cl=163759";
  public static final String REQUESTED_WITH = "ShockwaveFlash/23.0.0.185";
  protected volatile String currentCookies = "";
  protected volatile String authCookies = "";

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

  /**
   * Returns current session id not blocked.
   */
  public String getSessionForCheck() {
    return sessionId;
  }

  public String getPhishingTokenForCheck() {
    return sessionId;
  }

  public String getPhishingToken() {
    if (phishingToken == null) {
      log.error("phishingToken is null. Waiting to phishingToken will be updated");
      try {
        synchronized (this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
    return phishingToken;
  }

  public void setPhishingToken(String phishingToken) {
    this.phishingToken = phishingToken;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
    log.info("Updated session id to " + sessionId);
    synchronized (this) {
      this.notifyAll();
    }
  }

  public String getSessionId() {
    if (sessionId == null) {
      log.error("sessionId is null. Waiting until session will be updated");
      try {
        synchronized (this) {
          this.wait();
        }
      } catch (InterruptedException e) {
        log.error(e.getMessage());
      }
    }
    return sessionId;
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

  protected HttpPost createPostRequest(String url) {
    return createRequest(url, "POST");
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
    request.setHeader(HttpHeaders.HOST, "utas.external.s2.fut.ea.com");
    request.setHeader(HttpHeaders.REFERER, REFERER);
    request.setHeader(HttpHeaders.USER_AGENT,
        getUserAgent());
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader("X-Requested-With", REQUESTED_WITH);
    request.setHeader("X-UT-Embed-Error", "true");
    request.setHeader("Cookie", getCookies());
  }

  private String getCookies() {
    return currentCookies;
  }

  private String getUserAgent() {
    return "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36 OPR/40.0.2308.81";
  }

  protected HttpPost createRequest(String url) {
    return createRequest(url, "GET");
  }

  protected HttpPost createAuthRequest(String url) {
    HttpPost request = new HttpPost(url);
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma, br");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.8,uk;q=0.6");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    request.setHeader(HttpHeaders.HOST, "www.easports.com");
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader(HttpHeaders.REFERER,
        "https://www.easports.com/iframe/fut17/?locale=en_US&baseShowoffUrl=https%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app%2Fshow-off&guest_app_uri=http%3A%2F%2Fwww.easports.com%2Ffifa%2Fultimate-team%2Fweb-app");
    request.setHeader(HttpHeaders.USER_AGENT, getUserAgent());
    request.setHeader("Origin", "https://www.easports.com");
    request.setHeader("X-Requested-With", REQUESTED_WITH);
    request.setHeader("X-UT-Embed-Error", "true");
    request.setHeader("X-HTTP-Method-Override", "POST");
    request.setHeader("Easw-Session-Data-Nucleus-Id", getNucleusId());
    request.setHeader("X-UT-Route", "https://utas.external.s2.fut.ea.com:443");
    request.setHeader("X-UT-PHISHING-TOKEN", getPhishingToken());
    request.setHeader("Cookie", authCookies);
    return request;
  }


  public String getNucleusId() {
    return "2311254984";
  }

  private String toCookiesString(List<TokenUpdateRequest.Cookie> cookies) {
    List<String> collect = cookies.stream()
        .map(k -> k.getName() + "=" + k.getValue())
        .collect(Collectors.toList());
    return String.join(";", collect);
  }

  public void updateCookies(List<TokenUpdateRequest.Cookie> cookies) {
    currentCookies = toCookiesString(cookies);
  }

  public void updateAuthCookies(List<TokenUpdateRequest.Cookie> cookies) {
    authCookies = toCookiesString(cookies);
  }
}
