package com.liberty.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.typesafe.config.ConfigFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import akka.actor.ActorSystem;

/**
 * Created by Dmytro_Kovalskyi on 28.03.2016.
 */
@Configuration
@ComponentScan("com.liberty")
@EnableScheduling
@EnableWebSocket
@EnableMongoRepositories("com.liberty.repositories")
public class Config extends AbstractMongoConfiguration {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private SpringExtension springExtension;


  public static final int POOL_SIZE = 5;

  @Override
  protected String getDatabaseName() {
    return "fifa17";
  }

  @Override
  public Mongo mongo() throws Exception {
    return new MongoClient("127.0.0.1", 27017);
  }

  @Bean
  public GridFsTemplate gridFsTemplate() throws Exception {
    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
  }

  @Override
  protected String getMappingBasePackage() {
    return "com.liberty.model";
  }

  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(POOL_SIZE);
    return scheduler;
  }

  @Bean
  public ActorSystem actorSystem() {
    ActorSystem system = ActorSystem.create("LibertyActorSystem");
    springExtension.initialize(applicationContext);
    return system;
  }

  @Bean
  public com.typesafe.config.Config akkaConfiguration() {
    return ConfigFactory.load();
  }
}