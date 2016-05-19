package com.liberty;

import com.liberty.common.Config;
import com.liberty.service.MonitoringService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.IntStream;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public class RunMonitoring {

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    MonitoringService monitoringService = context.getBean(MonitoringService.class);
    monitoringService.monitor(20575);
    IntStream.range(1, 1000).forEach(i -> {
      monitoringService.updatePrices();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {


      }
    });
  }
}
