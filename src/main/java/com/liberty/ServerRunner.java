package com.liberty;

import com.liberty.config.Config;
import com.liberty.config.WebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@SpringBootApplication
public class ServerRunner {

    public static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) {
        System.getProperties().put("server.port", DEFAULT_PORT);
        ConfigurableApplicationContext context =
                SpringApplication.run(new Class<?>[]{Config.class, WebSocketConfig.class}, args);

        System.out.println("Application started...");
    }
}
