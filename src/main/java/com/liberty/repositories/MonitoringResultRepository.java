package com.liberty.repositories;

import com.liberty.model.MonitoringResult;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.05.2016.
 */
@Repository
public interface MonitoringResultRepository extends MongoRepository<MonitoringResult, Long> {

}
