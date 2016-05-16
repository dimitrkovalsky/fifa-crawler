package com.liberty.repositories;

import com.liberty.model.PlayerInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Dimitr
 * Date: 16.05.2016
 * Time: 21:54
 */
@Repository
public interface PlayerInfoRepository extends MongoRepository<PlayerInfo, Integer> {
}
