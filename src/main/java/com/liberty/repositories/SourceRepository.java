package com.liberty.repositories;

import com.liberty.model.Source;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Dmytro_Kovalskyi.
 * @since 20.05.2016.
 */
@Repository
public interface SourceRepository extends MongoRepository<Source, String> {

}
