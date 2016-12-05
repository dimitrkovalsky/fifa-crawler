package com.liberty.repositories;

import com.liberty.model.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 04.12.2016.
 */
@Repository
public interface UserSessionRepository extends MongoRepository<UserSession, Long> {

}
