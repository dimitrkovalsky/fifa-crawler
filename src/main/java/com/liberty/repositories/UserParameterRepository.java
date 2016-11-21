package com.liberty.repositories;

import com.liberty.model.UserParameters;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 19.11.2016.
 */
@Repository
public interface UserParameterRepository extends MongoRepository<UserParameters, Long> {

}
