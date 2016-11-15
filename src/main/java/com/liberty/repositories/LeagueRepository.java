package com.liberty.repositories;

import com.liberty.model.League;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Repository
public interface LeagueRepository extends MongoRepository<League, Long> {

    League findOneByAbbrName(String abbrName);
}
