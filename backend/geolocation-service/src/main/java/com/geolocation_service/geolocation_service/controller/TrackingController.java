package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.EnrichedPositionDTO;
import com.geolocation_service.geolocation_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour le tracking des bus avec informations enrichies
 * <p>
 * Ce contrôleur fournit des endpoints pour obtenir les positions des bus
 * enrichies avec les informations complètes provenant du schedule-service.
 */
@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * Récupère toutes les positions de bus enrichies avec les informations
     * complètes du bus depuis le schedule-service
     * <p>
     * Cet endpoint combine les données de géolocalisation (MongoDB) avec
     * les informations détaillées des bus (PostgreSQL via schedule-service)
     *
     * @return Liste des positions enrichies
     */
    @GetMapping("/positions-enriched")
    public ResponseEntity<List<EnrichedPositionDTO>> getEnrichedPositions() {
        log.info("GET /api/tracking/positions-enriched - Fetching enriched bus positions");
        
        try {
            List<EnrichedPositionDTO> enrichedPositions = trackingService.getEnrichedPositions();
            
            log.info("Successfully returned {} enriched positions", enrichedPositions.size());
            return ResponseEntity.ok(enrichedPositions);
            
        } catch (Exception e) {
            log.error("Error fetching enriched positions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère la position enrichie d'un bus spécifique
     *
     * @param busId ID du bus
     * @return Position enrichie du bus
     */
    @GetMapping("/positions-enriched/{busId}")
    public ResponseEntity<EnrichedPositionDTO> getEnrichedPositionByBusId(@PathVariable Long busId) {
        log.info("GET /api/tracking/positions-enriched/{} - Fetching position for bus", busId);
        
        try {
            List<EnrichedPositionDTO> allPositions = trackingService.getEnrichedPositions();
            
            EnrichedPositionDTO busPosition = allPositions.stream()
                    .filter(pos -> pos.getBusId().equals(busId))
                    .findFirst()
                    .orElse(null);
            
            if (busPosition != null) {
                log.info("Found position for bus {}", busId);
                return ResponseEntity.ok(busPosition);
            } else {
                log.warn("No position found for bus {}", busId);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error fetching position for bus {}: {}", busId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de santé pour vérifier que le service de tracking fonctionne
     *
     * @return Statut du service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tracking service is running");
    }
}

