package com.liberty.common;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Dmytro_Kovalskyi on 28.03.2016.
 */
@Configuration
@ComponentScan("com.liberty")
@EnableScheduling
@EnableMongoRepositories("com.liberty.repositories")
public class Config extends AbstractMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "fifa16";
  }

  @Override
  public Mongo mongo() throws Exception {
    return new MongoClient("127.0.0.1", 27017);
  }

  @Override
  protected String getMappingBasePackage() {
    return "com.liberty.model";
  }
}