package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Direction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectionRepository extends MongoRepository<Direction, String> {
    List<Direction> findByLigneIdLigne(String ligneId);
}