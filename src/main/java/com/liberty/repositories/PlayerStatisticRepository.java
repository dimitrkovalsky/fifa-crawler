package com.liberty.repositories;

import com.liberty.model.PlayerStatistic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.06.2016.
 */
@Repository
public interface PlayerStatisticRepository extends MongoRepository<PlayerStatistic, Long> {

}
