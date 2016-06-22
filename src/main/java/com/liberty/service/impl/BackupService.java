package com.liberty.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liberty.model.PlayerTradeStatus;
import com.liberty.repositories.PlayerTradeStatusRepository;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Dimitr Date: 21.06.2016 Time: 8:43
 */
@Service
public class BackupService {

  private static String BACKUP_PATH = "D:\\players.json";

  private PlayerTradeStatusRepository statusRepository;

  public void backup() throws IOException {
    List<PlayerTradeStatus> players = statusRepository.findAll();
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(new File(BACKUP_PATH), players);
  }

  public void restore() throws IOException {
    List<PlayerTradeStatus> players = statusRepository.findAll();
    ObjectMapper mapper = new ObjectMapper();

  }
}
