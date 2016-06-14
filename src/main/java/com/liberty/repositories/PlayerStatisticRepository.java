package com.liberty.repositories;

import com.liberty.model.market.PlayerStatistic;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 14.06.2016.
 */
public interface PlayerStatisticRepository extends MongoRepository<PlayerStatistic, Long> {

}
