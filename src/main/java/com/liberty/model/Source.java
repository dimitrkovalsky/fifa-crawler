package com.liberty.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dmytro_Kovalskyi.
 * @since 20.05.2016.
 */
@Data
@Document(collection = "source")
@AllArgsConstructor
@NoArgsConstructor
public class Source {

  @Id
  private String id;
  private String source;


  public Source(String source) {
    this.source = source;
  }

}
