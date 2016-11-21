package com.liberty.service.impl;

import com.liberty.common.TransactionType;
import com.liberty.model.Transaction;
import com.liberty.model.market.AuctionInfo;
import com.liberty.repositories.TransactionRepository;
import com.liberty.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 25.10.2016.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void logBuy(Long playerId, Long itemId, Long tradeId, Integer amount) {
        Transaction.TransactionRecord record = new Transaction.TransactionRecord(itemId, amount,
                System.currentTimeMillis(), TransactionType.BOUGHT_CARD);
        record.setTradeId(tradeId);
        logOperation(playerId, record);
    }

    @Override
    public void logSell(Long playerId, Long itemId, Long tradeId, Integer amount) {
        Transaction.TransactionRecord record = new Transaction.TransactionRecord(itemId, amount,
                System.currentTimeMillis(), TransactionType.SELL_CARD);
        record.setTradeId(tradeId);
        logOperation(playerId, record);
    }

    @Override
    public void logPlaceToMarket(Long playerId, Long itemId, Long tradeId, Integer startBid,
                                 Integer buyNow) {
        Transaction.TransactionRecord record = new Transaction.TransactionRecord();
        record.setItemId(itemId);
        record.setOperation(TransactionType.PLACED_TO_MARKET);
        record.setTimestamp(System.currentTimeMillis());
        record.setBuyNow(buyNow);
        record.setStartingBid(startBid);
        record.setTradeId(tradeId);
        logOperation(playerId, record);
    }

    @Override
    public void logBuyByRobot(AuctionInfo auctionInfo) {
        Transaction.TransactionRecord record = new Transaction.TransactionRecord(auctionInfo
                .getItemData().getId(), auctionInfo.getCurrentBid(), System.currentTimeMillis(),
                TransactionType.BOUGHT_BY_ROBOT);
        record.setTradeId(auctionInfo.getTradeId());
        logUniqueOperation(auctionInfo.getItemData().getAssetId(), record);
    }

    @Override
    public void logRelistOperation(Long playerId, Long itemId, Long tradeId, Integer startBid,
                                   Integer buyNow) {
        Transaction.TransactionRecord record = new Transaction.TransactionRecord();
        record.setItemId(itemId);
        record.setOperation(TransactionType.RELIST);
        record.setTimestamp(System.currentTimeMillis());
        record.setBuyNow(buyNow);
        record.setStartingBid(startBid);
        record.setTradeId(tradeId);
        logOperation(playerId, record);
    }

    private void logOperation(Long playerId, Transaction.TransactionRecord record) {
        Transaction transaction = transactionRepository.findOne(playerId);
        if (transaction == null) {
            transaction = new Transaction();
            transaction.setPlayerId(playerId);
        }
        List<Transaction.TransactionRecord> records = transaction.getRecords();
        records.add(record);
        transactionRepository.save(transaction);
    }

    private void logUniqueOperation(Long playerId, Transaction.TransactionRecord record) {
        Transaction transaction = transactionRepository.findOne(playerId);
        if (transaction == null) {
            transaction = new Transaction();
            transaction.setPlayerId(playerId);
        }
        List<Transaction.TransactionRecord> records = transaction.getRecords();
        if (!recordedPreviously(records, record)) {
            records.add(record);
        }
        transactionRepository.save(transaction);
    }

    private boolean recordedPreviously(List<Transaction.TransactionRecord> records,
                                       Transaction.TransactionRecord record) {
        return records.stream().anyMatch(r ->
                r.getTradeId().equals(record.getTradeId()) &&
                        r.getItemId().equals(record.getItemId()) && r.getOperation() == record.getOperation());
    }

}
