package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.HistoriquePosition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriquePositionRepository extends MongoRepository<HistoriquePosition, String> {
    List<HistoriquePosition> findByBusIdBus(String busId);
}
