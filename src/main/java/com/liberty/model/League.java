package com.liberty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document(collection = "leagues")
public class League implements Serializable {

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("abbrName")
    private String abbrName;

    @JsonProperty("imgUrl")
    private String imgUrl;

    @JsonProperty("name")
    private String name;


}