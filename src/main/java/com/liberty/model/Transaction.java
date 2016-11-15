package com.liberty.model;

import com.liberty.common.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "transactions")
public class Transaction {

    @Id
    private Long playerId;

    private List<TransactionRecord> records = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionRecord {

        private Long itemId;
        private Long tradeId;
        private Integer coins;
        private Long timestamp;
        private TransactionType operation;
        private Integer buyNow;
        private Integer startingBid;

        public TransactionRecord(Long itemId, Integer coins, Long timestamp, TransactionType operation) {
            this.itemId = itemId;
            this.coins = coins;
            this.timestamp = timestamp;
            this.operation = operation;
        }
    }
}
