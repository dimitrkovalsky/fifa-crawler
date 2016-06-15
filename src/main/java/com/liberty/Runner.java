package com.liberty;

import com.liberty.config.Config;
import com.liberty.service.TradeService;

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
    TradeService service = context.getBean(TradeService.class);
//    service.addToAutoBuy("Vardy", 208830L, 1000);
//    service.addToAutoBuy("Mahrez", 204485L, 1000);
    service.findMinPrice(208830L);
  }
}
