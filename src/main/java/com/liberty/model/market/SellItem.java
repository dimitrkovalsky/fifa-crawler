package com.liberty.model.market;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitr Date: 04.06.2016 Time: 16:55
 */
@Data
@NoArgsConstructor
public class SellItem {

    private List<ItemData> itemData = new ArrayList<>();

    public SellItem(long id) {
        ItemData data = new ItemData();
        data.setId(id);
        itemData.add(data);
    }

    public SellItem(long id, long tradeId) {
        ItemData data = new ItemData();
        data.setId(id);
        data.setTradeId(tradeId);
        itemData.add(data);
    }

    @Data
    public static class ItemData {

        Long id;
        Long tradeId;
        String pile = "trade";
        Boolean success;
    }
}
