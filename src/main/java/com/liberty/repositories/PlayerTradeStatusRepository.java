package com.liberty.repositories;

import com.liberty.model.PlayerTradeStatus;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Dimitr Date: 03.06.2016 Time: 20:11
 */
@Repository
public interface PlayerTradeStatusRepository extends
    MongoRepository<PlayerTradeStatus, Long> {

}
