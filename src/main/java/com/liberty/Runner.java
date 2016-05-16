package com.liberty;

import com.liberty.service.CrawlerService;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class Runner {

  public static void main(String[] args) {
    CrawlerService service = new CrawlerService();
    service.execute();
  }
}
