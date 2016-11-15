package com.liberty.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * User: Dimitr
 * Date: 22.10.2016
 * Time: 10:25
 */
@Data
@Document(collection = "tags")
@NoArgsConstructor
public class Tag implements Serializable {

    @Id
    private ObjectId id;

    private String name;


    public Tag(String name) {
        this.name = name;
    }
}
