package com.liberty;

import com.liberty.config.Config;
import com.liberty.service.CrawlerService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class Runner {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    CrawlerService service = context.getBean(CrawlerService.class);

    service.saveOthers();
    System.exit(0);
  }
}
