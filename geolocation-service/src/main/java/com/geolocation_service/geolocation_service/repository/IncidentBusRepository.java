package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.IncidentBus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IncidentBusRepository extends MongoRepository<IncidentBus, String> {
    List<IncidentBus> findByBusIdBus(String busId);
    List<IncidentBus> findByStatut(String statut);
}
