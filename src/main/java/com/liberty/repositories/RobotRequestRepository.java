package com.liberty.repositories;

import com.liberty.model.RobotRequest;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Repository
public interface RobotRequestRepository extends MongoRepository<RobotRequest, Integer> {

}
