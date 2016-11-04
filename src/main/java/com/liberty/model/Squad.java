package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

import static com.liberty.common.DateHelper.toReadableString;

@Data
@Document(collection = "squads")
public class Squad implements Serializable {

  @Id
  private Long id;

  private List<Long> playerIds;

  private LocalDateTime innerDate;

  private String squadGroup;

  private String squadName;

  public String getDate() {
    return toReadableString(innerDate);
  }
}