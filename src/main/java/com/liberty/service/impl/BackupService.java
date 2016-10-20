package com.liberty.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.repositories.PlayerTradeStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * User: Dimitr Date: 21.06.2016 Time: 8:43
 */
@Service
public class BackupService {

  private static String BACKUP_PATH = System.getProperty("user.dir") +
      "\\src\\main\\resources\\players.json";

  //private static String BACKUP_PATH = "D:\\players.json";

  @Autowired
  private PlayerTradeStatusRepository statusRepository;

  public void backup() throws IOException {
    List<PlayerTradeStatus> players = statusRepository.findAll();
    ObjectMapper mapper = getObjectMapper();


    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(BACKUP_PATH), players);
    System.out.println("Backup completed... ");
    System.out.println("Data stored in " + BACKUP_PATH);
    System.exit(1);
  }

  private ObjectMapper getObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    JavaTimeModule javaTimeModule=new JavaTimeModule();
    // Hack time module to allow 'Z' at the end of string (i.e. javascript json's)
    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
        DateTimeFormatter.ISO_DATE_TIME));
    mapper.registerModule(javaTimeModule);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }

  public void restore() throws IOException {
    ObjectMapper mapper = getObjectMapper();

    List<PlayerTradeStatus> players = mapper
        .readValue(new File(BACKUP_PATH), new TypeReference<List<PlayerTradeStatus>>() {
        });
    statusRepository.deleteAll();
    statusRepository.save(players);
    System.out.println("Backup restored...");
    System.out.println("Data restored from " + BACKUP_PATH);
    System.exit(1);
  }
}
