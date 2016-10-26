package com.liberty.repositories;

import com.liberty.model.PriceHistory;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PriceHistoryRepository extends MongoRepository<PriceHistory, Long> {

}
