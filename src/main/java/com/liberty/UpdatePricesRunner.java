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
public class UpdatePricesRunner {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    TradeService tradeService = context.getBean(TradeService.class);
    tradeService.updatePrices();
    System.out.println("Prices updated successfully");
    System.exit(1);
  }
}
