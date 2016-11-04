package com.liberty.repositories;

import com.liberty.model.SquadStatistic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SquadStatisticRepository extends MongoRepository<SquadStatistic, Long> {

}
