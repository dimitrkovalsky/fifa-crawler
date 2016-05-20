package com.liberty.repositories;

import com.liberty.model.PlayerMonitoring;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dmytro_Kovalskyi.
 * @since 17.05.2016.
 */
@Repository
public interface PlayerMonitoringRepository extends MongoRepository<PlayerMonitoring, Long> {

  List<PlayerMonitoring> findAllBySource(String source);
}
