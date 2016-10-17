package com.liberty.common;

import java.util.Random;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.10.2016.
 */
@Slf4j
public class DelayHelper {

  private static int MIN_DELAY = 2000;
  private static int MAX_DELAY = 5000;

  public static void wait(int millis) {
    int delay = new Random().nextInt(MAX_DELAY - MIN_DELAY) + MIN_DELAY + millis;
    log.info("Waiting " + delay + " millis");
    delay(delay);
  }

  public static void waitStrict(int delay) {
    delay(delay);
  }

  private static void delay(int delay) {
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      log.error(e.getMessage());
    }
  }
}
