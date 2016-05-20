package com.liberty.repositories;

import com.liberty.model.PlayerProfile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.05.2016.
 */
@Repository
public interface PlayerProfileRepository extends MongoRepository<PlayerProfile, Long> {
//  List<PlayerProfile> findAllByPlayerInfoSource(String source);
}
