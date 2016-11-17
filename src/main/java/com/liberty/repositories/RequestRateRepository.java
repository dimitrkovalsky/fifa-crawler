package com.liberty.repositories;

import com.liberty.model.RequestRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Repository
public interface RequestRateRepository extends MongoRepository<RequestRate, Long> {
    List<RequestRate> findAllByTimestampBetween(long from, long to);
}
