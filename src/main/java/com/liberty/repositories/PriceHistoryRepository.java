package com.liberty.repositories;

import com.liberty.model.PriceHistory;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.05.2016.
 */
@Repository
public interface PriceHistoryRepository extends MongoRepository<PriceHistory, Long> {
}
