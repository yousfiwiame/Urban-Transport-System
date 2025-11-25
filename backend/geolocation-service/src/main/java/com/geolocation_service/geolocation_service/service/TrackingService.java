package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.dto.EnrichedPositionDTO;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour enrichir les positions de bus avec les informations
 * complètes provenant du schedule-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingService {

    private final PositionBusRepository positionBusRepository;
    private final RestTemplate restTemplate;

    @Value("${schedule.service.url:http://schedule-service:8082}")
    private String scheduleServiceUrl;

    /**
     * Récupère toutes les positions de bus et les enrichit avec les informations
     * complètes du bus depuis le schedule-service
     *
     * @return Liste des positions enrichies
     */
    public List<EnrichedPositionDTO> getEnrichedPositions() {
        log.info("Fetching enriched bus positions");
        
        // Récupérer toutes les positions depuis MongoDB
        List<PositionBus> positions = positionBusRepository.findAll();
        log.info("Found {} positions in database", positions.size());

        List<EnrichedPositionDTO> enrichedPositions = new ArrayList<>();

        for (PositionBus position : positions) {
            try {
                // Extraire busId depuis l'objet Bus de MongoDB (ancien système)
                // ou depuis un champ busId direct si disponible
                Long busId = extractBusId(position);
                
                if (busId == null) {
                    log.warn("Position {} has no valid busId, skipping", position.getIdPosition());
                    continue;
                }

                // Appeler schedule-service pour obtenir les infos complètes du bus
                EnrichedPositionDTO.BusInfoDTO busInfo = fetchBusInfo(busId);

                // Construire le DTO enrichi
                EnrichedPositionDTO enriched = EnrichedPositionDTO.builder()
                        .idPosition(position.getIdPosition())
                        .busId(busId)
                        .latitude(position.getLatitude())
                        .longitude(position.getLongitude())
                        .altitude(position.getAltitude())
                        .precision(position.getPrecision())
                        .vitesse(position.getVitesse())
                        .direction(position.getDirection())
                        .timestamp(position.getTimestamp())
                        .bus(busInfo)
                        .build();

                enrichedPositions.add(enriched);
                
            } catch (Exception e) {
                log.error("Error enriching position {}: {}", position.getIdPosition(), e.getMessage());
                // Continue avec la prochaine position en cas d'erreur
            }
        }

        log.info("Successfully enriched {} positions", enrichedPositions.size());
        return enrichedPositions;
    }

    /**
     * Extrait l'ID du bus depuis la position
     * Compatible avec l'ancien système (Bus MongoDB) et le nouveau (busId direct)
     */
    @SuppressWarnings("deprecation")
    private Long extractBusId(PositionBus position) {
        // Priorité 1: Utiliser le nouveau champ busId direct
        if (position.getBusId() != null) {
            return position.getBusId();
        }
        
        // Priorité 2: Fallback sur l'ancien système (Bus MongoDB)
        if (position.getBus() != null && position.getBus().getIdBus() != null) {
            try {
                // Si idBus est un String dans MongoDB, le convertir en Long
                return Long.parseLong(position.getBus().getIdBus());
            } catch (NumberFormatException e) {
                log.warn("Invalid busId format in MongoDB Bus: {}", position.getBus().getIdBus());
            }
        }
        
        return null;
    }

    /**
     * Appelle le schedule-service pour obtenir les informations complètes d'un bus
     *
     * @param busId ID du bus
     * @return Informations complètes du bus
     */
    private EnrichedPositionDTO.BusInfoDTO fetchBusInfo(Long busId) {
        String url = scheduleServiceUrl + "/api/buses/" + busId;
        
        try {
            log.debug("Calling schedule-service: {}", url);
            
            // Appel REST vers schedule-service
            BusResponse busResponse = restTemplate.getForObject(url, BusResponse.class);
            
            if (busResponse == null) {
                log.warn("Bus {} not found in schedule-service", busId);
                return createDefaultBusInfo(busId);
            }

            // Mapper vers notre DTO
            return EnrichedPositionDTO.BusInfoDTO.builder()
                    .id(busResponse.getId())
                    .busNumber(busResponse.getBusNumber())
                    .licensePlate(busResponse.getLicensePlate())
                    .manufacturer(busResponse.getManufacturer())
                    .model(busResponse.getModel())
                    .year(busResponse.getYear())
                    .capacity(busResponse.getCapacity())
                    .status(busResponse.getStatus())
                    .seatingCapacity(busResponse.getSeatingCapacity())
                    .standingCapacity(busResponse.getStandingCapacity())
                    .hasWifi(busResponse.getHasWifi())
                    .hasAirConditioning(busResponse.getHasAirConditioning())
                    .hasGPS(busResponse.getHasGPS())
                    .notes(busResponse.getNotes())
                    .build();
                    
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Bus {} not found in schedule-service (404)", busId);
            return createDefaultBusInfo(busId);
        } catch (Exception e) {
            log.error("Error fetching bus {} from schedule-service: {}", busId, e.getMessage());
            return createDefaultBusInfo(busId);
        }
    }

    /**
     * Crée un BusInfo par défaut quand le bus n'est pas trouvé dans schedule-service
     */
    private EnrichedPositionDTO.BusInfoDTO createDefaultBusInfo(Long busId) {
        return EnrichedPositionDTO.BusInfoDTO.builder()
                .id(busId)
                .busNumber("BUS-" + busId)
                .status("UNKNOWN")
                .build();
    }

    /**
     * Classe interne pour mapper la réponse du schedule-service
     */
    @lombok.Data
    private static class BusResponse {
        private Long id;
        private String busNumber;
        private String licensePlate;
        private String manufacturer;
        private String model;
        private Integer year;
        private Integer capacity;
        private String status;
        private Integer seatingCapacity;
        private Integer standingCapacity;
        private Boolean hasWifi;
        private Boolean hasAirConditioning;
        private Boolean hasGPS;
        private String notes;
    }
}

