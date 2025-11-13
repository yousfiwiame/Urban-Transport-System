package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.PositionBus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PositionBusRepository extends MongoRepository<PositionBus, String> {
    List<PositionBus> findByBusIdBus(String busId);

    // Trouver la dernière position d'un bus (CORRIGÉ)
    List<PositionBus> findByBusIdBusOrderByTimestampDesc(String busId);

    // Trouver les positions d'un bus dans un intervalle de temps (CORRIGÉ)
    List<PositionBus> findByBusIdBusAndTimestampBetweenOrderByTimestampAsc(
            String busId, LocalDateTime start, LocalDateTime end);
}