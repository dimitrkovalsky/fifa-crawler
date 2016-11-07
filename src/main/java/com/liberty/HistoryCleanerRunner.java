package com.liberty;

import com.liberty.config.Config;
import com.liberty.service.ConfigService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class HistoryCleanerRunner {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    ConfigService configService = context.getBean(ConfigService.class);
    configService.cleanHistory();
    System.out.println("History cleaned successfully");
    System.exit(0);
  }
}
