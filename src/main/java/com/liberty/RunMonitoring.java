package com.liberty;

import com.liberty.common.Config;
import com.liberty.service.MonitoringService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Dmytro_Kovalskyi.
 * @since 18.05.2016.
 */
public class RunMonitoring {

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    context.getBean(MonitoringService.class).monitor(20575);
  }
}
