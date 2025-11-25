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
    /**
     * Trouver toutes les positions d'un bus par son ID (nouveau système)
     */
    List<PositionBus> findByBusId(Long busId);

    /**
     * Trouver la dernière position d'un bus (nouveau système)
     */
    List<PositionBus> findByBusIdOrderByTimestampDesc(Long busId);

    /**
     * Trouver les positions d'un bus dans un intervalle de temps (nouveau système)
     */
    List<PositionBus> findByBusIdAndTimestampBetweenOrderByTimestampAsc(
            Long busId, LocalDateTime start, LocalDateTime end);

    /**
     * Trouver la dernière position d'un bus (nouveau système) - première seulement
     */
    Optional<PositionBus> findFirstByBusIdOrderByTimestampDesc(Long busId);
}