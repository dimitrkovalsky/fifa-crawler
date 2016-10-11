package com.liberty.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liberty.processors.pojo.ImageUrls;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

import lombok.Data;

@Data
@Document(collection = "clubs")
public class Club implements Serializable{

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("imageUrls")
    private ImageUrls imageUrls;

    @JsonProperty("abbrName")
    private String abbrName;

    @JsonProperty("imgUrl")
    private String imgUrl;

    @JsonProperty("name")
    private String name;


}