package com.liberty;

import com.liberty.common.Config;
import com.liberty.service.CrawlerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class Runner {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        context.getBean(CrawlerService.class).execute();
    }
}
