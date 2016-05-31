package com.liberty.robot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * User: Dimitr Date: 31.05.2016 Time: 21:22
 */
public class Yanka {

  public static void main(String[] args) throws AWTException {
    FirefoxBinary firefoxbin = new FirefoxBinary(new File("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"));

    WebDriver driver = new FirefoxDriver(firefoxbin, new FirefoxProfile());


    driver.manage().window().maximize();

    driver.get("https://www.easports.com/fifa/ultimate-team/web-app");
    driver.findElement(By.id("email")).sendKeys("dimitrkovalsky@ukr.net");
    driver.findElement(By.id("password")).sendKeys("Dimitr3101");
    driver.findElement(By.id("btnLogin")).click();
    WebDriverWait wait = new WebDriverWait(driver, 60);
    System.out.println("Continue");
    // Create object of Robot class
    Robot r = new Robot();

    // Press Enter
    r.keyPress(KeyEvent.VK_ENTER);

    // Release Enter
    r.keyRelease(KeyEvent.VK_ENTER);
    r.mouseMove(300,300);

  }
}
