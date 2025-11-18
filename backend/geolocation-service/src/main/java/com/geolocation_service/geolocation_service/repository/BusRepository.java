package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Bus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusRepository extends MongoRepository<Bus, String> {
    Bus findByImmatriculation(String immatriculation);
    List<Bus> findByLigneActuelleIdLigne(String ligneId);
    List<Bus> findByDirectionActuelleIdDirection(String directionId);
    List<Bus> findByLigneActuelleIdLigneAndDirectionActuelleIdDirection(String ligneId, String directionId);
}