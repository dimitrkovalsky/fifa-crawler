package com.liberty.model;

import com.liberty.service.impl.HistoryServiceImpl;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * User: Dimitr
 * Date: 04.11.2016
 * Time: 8:53
 */
@Data
@Document(collection = "squad_statistic")
public class SquadStatistic {

  @Id
  private Long id;
  private Map<Long, HistoryServiceImpl.HistoryPoint> history = new HashMap<>();
}
