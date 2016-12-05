package com.liberty.model;

import com.liberty.common.Platform;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: Dimitr
 * Date: 04.12.2016
 * Time: 11:53
 */
@Data
@Document(collection = "user_session")
public class UserSession {
    @Id
    private Long userId;

    private Platform platform;
}
