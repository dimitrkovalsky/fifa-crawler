package com.liberty.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.repositories.PlayerTradeStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
    ObjectMapper mapper = new ObjectMapper();
    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(BACKUP_PATH), players);
    System.out.println("Backup completed... ");
    System.out.println("Data stored in " + BACKUP_PATH);
    System.exit(1);
  }

  public void restore() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
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
