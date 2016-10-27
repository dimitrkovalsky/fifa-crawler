package com.liberty;

import com.liberty.config.Config;
import com.liberty.service.PriceService;

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
    PriceService priceService = context.getBean(PriceService.class);
    priceService.updatePrices();
    System.out.println("Prices updated successfully");
    System.exit(1);
  }
}
