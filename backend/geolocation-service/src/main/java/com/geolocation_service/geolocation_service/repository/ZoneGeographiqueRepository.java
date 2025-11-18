package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.ZoneGeographique;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneGeographiqueRepository extends MongoRepository<ZoneGeographique, String> {
    ZoneGeographique findByNom(String nom);
}
