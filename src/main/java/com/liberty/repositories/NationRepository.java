package com.liberty.repositories;

import com.liberty.model.Nation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 11.10.2016.
 */
@Repository
public interface NationRepository extends MongoRepository<Nation, Long> {

}
