package com.liberty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@SpringBootApplication
public class ServerRunner {

  public static void main(String[] args) {
    System.getProperties().put("server.port", 5555);

    SpringApplication.run(ServerRunner.class, args);
    System.out.println("Application started...");
  }
}
