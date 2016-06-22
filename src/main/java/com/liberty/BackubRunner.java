package com.liberty;

import com.liberty.config.Config;
import com.liberty.service.impl.BackupService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * @author Dmytro_Kovalskyi.
 * @since 16.05.2016.
 */
public class BackubRunner {

  public static void main(String[] args) throws IOException {
    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
    BackupService service = context.getBean(BackupService.class);
//    service.addToAutoBuy("Vardy", 208830L, 1000);
//    service.addToAutoBuy("Mahrez", 204485L, 1000);
//    service.backup();
    service.restore();
  }
}
