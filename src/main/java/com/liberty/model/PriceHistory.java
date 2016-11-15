package com.liberty.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "price_history")
@NoArgsConstructor
public class PriceHistory {

    @Id
    private Long playerId;

    private Map<Long, Map<Integer, Integer>> history = new HashMap<>();
}
