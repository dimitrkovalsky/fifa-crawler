package com.liberty.model;

import com.liberty.common.PriceHelper;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

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
    private Map<Long, PriceHelper.HistoryPoint> history = new HashMap<>();
}
