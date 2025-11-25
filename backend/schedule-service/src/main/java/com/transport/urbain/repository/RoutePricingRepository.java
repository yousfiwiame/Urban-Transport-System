package com.transport.urbain.repository;

import com.transport.urbain.model.RoutePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository pour gérer les prix des routes
 */
@Repository
public interface RoutePricingRepository extends JpaRepository<RoutePricing, Long> {

    /**
     * Récupérer le pricing actif pour une route
     */
    @Query("SELECT rp FROM RoutePricing rp WHERE rp.routeId = :routeId " +
           "AND rp.active = true " +
           "AND (rp.effectiveFrom IS NULL OR rp.effectiveFrom <= :now) " +
           "AND (rp.effectiveUntil IS NULL OR rp.effectiveUntil >= :now) " +
           "ORDER BY rp.createdAt DESC")
    Optional<RoutePricing> findActiveByRouteId(@Param("routeId") Long routeId, 
                                                 @Param("now") LocalDateTime now);

    /**
     * Récupérer tous les pricings pour une route
     */
    Optional<RoutePricing> findFirstByRouteIdAndActiveTrue(Long routeId);
}

