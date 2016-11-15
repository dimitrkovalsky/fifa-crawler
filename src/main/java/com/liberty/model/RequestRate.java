package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Dmytro_Kovalskyi.
 * @since 07.11.2016.
 */
@Data
@Document(collection = "request_rate")
public class RequestRate {

    @Id
    private long timestamp;

    private int requestPerMinute;

}
