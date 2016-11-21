package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: Dimitr
 * Date: 19.11.2016
 * Time: 12:33
 */
@Data
@Document(collection = "user_parameters")
public class UserParameters {
    @Id
    private Long userId;
    private boolean robotEnabled;
    private boolean autoBuyEnabled;
    private boolean autoSellEnabled;
    private boolean noActivityEnabled;
    private boolean autoSellRelistMinerEnabled;
    private boolean autoTradeEnabled;
    private boolean autoTradeOnlyActivePlayer;

    public void disableAll() {
        robotEnabled = false;
        autoBuyEnabled = false;
        autoSellEnabled = false;
        noActivityEnabled = false;
        autoSellRelistMinerEnabled = false;
        autoTradeEnabled = false;
        autoTradeOnlyActivePlayer = false;
    }
}
