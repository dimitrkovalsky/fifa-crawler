package com.liberty.common;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.liberty.common.LoggingUtil.error;


/**
 * Created by Dmytro_Kovalskyi on 17.02.2016.
 */
@Slf4j
public class RequestHelper {

  public static final String PHANTOMJS_EXE_PATH =
      "D:\\programming\\frameworks\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";

  public static InputStream executeRequest(String url) {
    try {
      final HttpParams httpParams = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
      HttpClient client = new DefaultHttpClient(httpParams);
      HttpGet request = new HttpGet(url);
      HttpResponse response = client.execute(request);
      return response.getEntity().getContent();
    } catch (Exception e) {
      error(null, e);
    }
    return null;
  }

  public static void executeRequestAndShowResult(String url) {
    System.out.println(executeRequestAndGetResult(url));
  }

  public static String executeRequestAndGetResult(String url) {
    return readResult(executeRequest(url));
  }

  public static String readResult(InputStream inputStream) {
    try {
      if (inputStream == null) {
        return "";
      }
      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    } catch (Exception e) {
      error(RequestHelper.class, "Content unavailable for : " + inputStream);
      return "";
    }
  }

  public static void saveToFile(String fileName, String data) {
    try {
      Path targetPath = new File(fileName).toPath();
      if (!Files.exists(targetPath.getParent())) {
        Files.createDirectories(targetPath.getParent());
      }
      Files.write(targetPath, data.getBytes(), StandardOpenOption.CREATE);
    } catch (Exception e) {
      error(RequestHelper.class, e);
    }
  }

  public static void saveToFile(String fileName, InputStream stream) throws IOException {
    Path targetPath = new File(fileName).toPath();
    if (!Files.exists(targetPath.getParent())) {
      Files.createDirectories(targetPath.getParent());
    }
    Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    stream.close();
  }

  public static String executeWithJs(String url) {
    WebDriver driver = null;
    try {
      driver = new PhantomJSDriver(getDriverConfig());
      log.info("Trying to get content from : " + url);
      driver.get(url);

      String source = driver.getPageSource();
      return source;
    } catch (Exception e) {
      log.error("Url is not valid : " + url);
      return "";
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  public static void execute() throws IOException {
    String URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/tradepile";
    HttpClient client = new org.apache.http.impl.client.ContentEncodingHttpClient();
    HttpPost request = new HttpPost(URL);
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "Accept-Language");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    //  request.setHeader(HttpHeaders.CONTENT_LENGTH, "1");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
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
    request.setHeader("X-UT-PHISHING-TOKEN", "2079335444359560537");
    request.setHeader("X-UT-SID", "b3b78b1b-00e8-47fe-9516-84df1345860e");
    request.setHeader("Cookie","optimizelyEndUserId=oeu1450945658852r0.39485638681799173; __utma=103303007.2105186675.1448875174.1448875174.1451291418.2; __utmz=103303007.1451291418.2.2.utmcsr=accounts.ea.com|utmccn=(referral)|utmcmd=referral|utmcct=/connect/logout; _nx_mpcid=9ddb98a4-ab3e-4c9a-a223-6764be03113f; utag_main=_st:1464791605501$v_id:0151d319be6300099ce81dcd82090c07e006e07600bd0$_sn:4$_ss:0$_pn:2%3Bexp-session$ses_id:1464789794804%3Bexp-session; optimizelySegments=%7B%222200840229%22%3A%22opera%22%2C%222207560119%22%3A%22false%22%2C%222209790291%22%3A%22none%22%2C%222215600082%22%3A%22search%22%7D; optimizelyBuckets=%7B%7D; _ga=GA1.2.2105186675.1448875174");
//    request.setHeader(HttpHeaders.COO, "application/json");
    HttpResponse response = client.execute(request);
    InputStream content = response.getEntity().getContent();
    System.out.println("CONTENT>>>> " + readResult(content));
//    System.out.println(execute.getEntity());
  }

  public static void execute2() throws IOException {
    String URL = "https://utas.s2.fut.ea.com/ut/game/fifa16/transfermarket?num=16&type=player&maxb=3000&maskedDefId=201510&start=0";
    HttpClient client = new ContentEncodingHttpClient();
    HttpPost request = new HttpPost(URL);
    request.setHeader(HttpHeaders.ACCEPT, "application/json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, lzma");
    request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "Accept-Language");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
    //  request.setHeader(HttpHeaders.CONTENT_LENGTH, "1");
    request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
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
    request.setHeader("X-UT-PHISHING-TOKEN", "9002495091363405202");
    request.setHeader("X-UT-SID", "aeeac1b4-824e-4e47-a9a2-83e20c13d7e1");
//    request.setHeader("Cookie","optimizelyEndUserId=oeu1450945658852r0.39485638681799173; __utma=103303007.2105186675.1448875174.1448875174.1451291418.2; __utmz=103303007.1451291418.2.2.utmcsr=accounts.ea.com|utmccn=(referral)|utmcmd=referral|utmcct=/connect/logout; _nx_mpcid=9ddb98a4-ab3e-4c9a-a223-6764be03113f; utag_main=_st:1464791605501$v_id:0151d319be6300099ce81dcd82090c07e006e07600bd0$_sn:4$_ss:0$_pn:2%3Bexp-session$ses_id:1464789794804%3Bexp-session; optimizelySegments=%7B%222200840229%22%3A%22opera%22%2C%222207560119%22%3A%22false%22%2C%222209790291%22%3A%22none%22%2C%222215600082%22%3A%22search%22%7D; optimizelyBuckets=%7B%7D; _ga=GA1.2.2105186675.1448875174");
//    request.setHeader(HttpHeaders.COO, "application/json");
    HttpResponse response = client.execute(request);
    InputStream content = response.getEntity().getContent();
    System.out.println("CONTENT>>>> " + readResult(content));
//    System.out.println(execute.getEntity());
  }

  public static void main(String[] args) throws IOException {
    execute2();
  }

  static {
    Logger.getLogger(PhantomJSDriverService.class.getName()).setLevel(Level.OFF);
  }

  private static DesiredCapabilities getDriverConfig() {
    DesiredCapabilities caps = new DesiredCapabilities();
    caps.setJavascriptEnabled(true);
    ArrayList<String> cliArgsCap = new ArrayList<String>();
    cliArgsCap.add("--webdriver-loglevel=NONE");
    caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
        PHANTOMJS_EXE_PATH);
    caps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
    return caps;
  }
}
