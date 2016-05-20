package com.liberty;

import com.liberty.common.Config;
import com.liberty.service.CrawlerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@SpringBootApplication
public class ServerRunner {

  public static void main(String[] args) {
    System.getProperties().put("server.port", 5555);

    ConfigurableApplicationContext context = SpringApplication.run(Config.class, args);
    context.getBean(CrawlerService.class).monitorInforms();
    System.out.println("Application started...");
  }
}
