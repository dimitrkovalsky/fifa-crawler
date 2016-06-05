package com.liberty.robot;


import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.awt.*;
import java.util.Set;

/**
 * @author Dmytro_Kovalskyi.
 * @since 01.06.2016.
 */
public class FifaRobot {

  public static void main(String[] args) throws AWTException, InterruptedException {

    WebDriver driver = getChromeWebDriver();

    driver.manage().window().maximize();

    // driver.get("https://google.com");
    driver.get("https://www.easports.com/fifa/ultimate-team/web-app");
    Thread.sleep(10000);
    Set<Cookie> cookies = driver.manage().getCookies();
    System.out.println("Size: " + cookies.size());


    CookieStore cookieStore = seleniumCookiesToCookieStore(driver);
    DefaultHttpClient httpClient = new DefaultHttpClient();
    httpClient.setCookieStore(cookieStore);

//    HttpPut httpPost = new HttpPut("");
//    System.out.println("Downloding file form: " + downloadUrl);
//    HttpResponse response = httpClient.execute(httpGet);
  }

  private static WebDriver getFirefoxWebDriver() {
    ProfilesIni profile = new ProfilesIni();
    FirefoxProfile ffprofile = profile.getProfile("default");
    System.out.println("PROFILE" + ffprofile);
    return new FirefoxDriver(ffprofile);
  }

  private static WebDriver getOperaWebDriver() {
    String operaPath = "C:\\Program Files (x86)\\Opera\\launcher.exe";
//    System.setProperty("opera.launcher", operaPath);
    System.setProperty("webdriver.opera.driver", operaPath);
//    OperaProfile profile = new OperaProfile("C:\\Users\\Dmytro_Kovalskyi\\AppData\\Roaming\\Opera Software\\Opera Stable");
//    DesiredCapabilities capabilities = DesiredCapabilities.opera();
//    capabilities.setCapability("opera.profile", profile);
//    capabilities.setCapability("opera.launcher", operaPath);
//    return new OperaDriver(capabilities);
    return null;
  }

  private static WebDriver getChromeWebDriver() {
    String pathToChrome = "D:\\programming\\frameworks\\chromedriver.exe";
    System.setProperty("webdriver.chrome.driver", pathToChrome);
    ChromeOptions options = new ChromeOptions();
    String profilePath =
        "C:\\Users\\Dmytro_Kovalskyi\\AppData\\Local\\Google\\Chrome\\User Data";
    options.addArguments("user-data-dir=" + profilePath);
    options.addArguments("user-data-dir=" + profilePath);
    options.addArguments("--start-maximized");
    DesiredCapabilities capability = DesiredCapabilities.chrome();
    capability.setCapability(ChromeOptions.CAPABILITY, options);

    return new ChromeDriver(capability);
  }

  private static CookieStore seleniumCookiesToCookieStore(WebDriver driver) {

    Set<Cookie> seleniumCookies = driver.manage().getCookies();
    CookieStore cookieStore = new BasicCookieStore();

    for (Cookie seleniumCookie : seleniumCookies) {
      BasicClientCookie basicClientCookie =
          new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
      basicClientCookie.setDomain(seleniumCookie.getDomain());
      basicClientCookie.setExpiryDate(seleniumCookie.getExpiry());
      basicClientCookie.setPath(seleniumCookie.getPath());
      cookieStore.addCookie(basicClientCookie);
    }

    return cookieStore;
  }
}
