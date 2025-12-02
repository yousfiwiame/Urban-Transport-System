package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.*;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.service.PositionBusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/positions")
@Slf4j
@CrossOrigin(origins = "*")
public class PositionBusController {

    private final PositionBusService positionBusService;

    public PositionBusController(PositionBusService positionBusService) {
        this.positionBusService = positionBusService;
    }

    @GetMapping
    public List<PositionBusDTO> getAllPositions() {
        return positionBusService.getAllPositions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/bus/{busId}")
    public List<PositionBusDTO> getPositionsByBus(@PathVariable Long busId) {
        return positionBusService.getPositionsByBusId(busId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<PositionBus> addPosition(@RequestBody PositionBus positionBus) {
        // Validate required fields - check for valid coordinate ranges
        if (positionBus.getLatitude() < -90 || positionBus.getLatitude() > 90 ||
            positionBus.getLongitude() < -180 || positionBus.getLongitude() > 180) {
            log.error("Invalid position data: latitude or longitude out of range");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            PositionBus saved = positionBusService.addPosition(positionBus);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Error saving position: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint simplifié pour l'application conducteur
     * Crée une nouvelle position GPS avec seulement le busId et les coordonnées
     * 
     * @param request Données GPS depuis l'application conducteur
     * @return Position créée
     */
    @PostMapping("/driver")
    public ResponseEntity<PositionBus> createPositionFromDriver(@RequestBody CreatePositionRequest request) {
        log.info("Receiving GPS position from driver for bus {}: ({}, {}), speed: {} km/h", 
                request.getBusId(), request.getLatitude(), request.getLongitude(), request.getVitesse());
        
        try {
            // Créer une nouvelle position avec le nouveau système (busId direct)
            PositionBus position = new PositionBus();
            position.setIdPosition(UUID.randomUUID().toString());
            position.setBusId(request.getBusId());
            position.setLatitude(request.getLatitude());
            position.setLongitude(request.getLongitude());
            position.setAltitude(request.getAltitude() != null ? request.getAltitude() : 0.0);
            position.setPrecision(request.getPrecision() != null ? request.getPrecision() : 10.0);
            position.setVitesse(request.getVitesse());
            position.setDirection(request.getDirection());
            position.setTimestamp(LocalDateTime.now());
            
            // Sauvegarder la position
            PositionBus savedPosition = positionBusService.addPosition(position);
            
            log.info("Position saved successfully with ID: {}", savedPosition.getIdPosition());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPosition);
            
        } catch (Exception e) {
            log.error("Error saving position for bus {}: {}", request.getBusId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @SuppressWarnings("deprecation")
    private PositionBusDTO convertToDTO(PositionBus position) {
        PositionBusDTO dto = new PositionBusDTO();
        dto.setIdPosition(position.getIdPosition());
        dto.setBusId(position.getBusId());
        dto.setLatitude(position.getLatitude());
        dto.setLongitude(position.getLongitude());
        dto.setAltitude(position.getAltitude());
        dto.setPrecision(position.getPrecision());
        dto.setVitesse(position.getVitesse());
        dto.setDirection(position.getDirection());
        dto.setTimestamp(position.getTimestamp());

        if (position.getBus() != null) {
            BusDTO busDTO = new BusDTO();
            busDTO.setIdBus(position.getBus().getIdBus());
            busDTO.setImmatriculation(position.getBus().getImmatriculation());
            busDTO.setModele(position.getBus().getModele());
            busDTO.setMarque(position.getBus().getMarque());
            busDTO.setCapacite(position.getBus().getCapacite());
            busDTO.setAnnee(position.getBus().getAnnee());
            busDTO.setStatut(position.getBus().getStatut());

            if (position.getBus().getLigneActuelle() != null) {
                LigneBusDTO ligneDTO = new LigneBusDTO();
                ligneDTO.setIdLigne(position.getBus().getLigneActuelle().getIdLigne());
                ligneDTO.setNumeroLigne(position.getBus().getLigneActuelle().getNumeroLigne());
                ligneDTO.setNomLigne(position.getBus().getLigneActuelle().getNomLigne());
                ligneDTO.setCouleur(position.getBus().getLigneActuelle().getCouleur());
                busDTO.setLigneActuelle(ligneDTO);
            }

            if (position.getBus().getDirectionActuelle() != null) {
                DirectionDTO directionDTO = new DirectionDTO();
                directionDTO.setIdDirection(position.getBus().getDirectionActuelle().getIdDirection());
                directionDTO.setNomDirection(position.getBus().getDirectionActuelle().getNomDirection());
                directionDTO.setPointDepart(position.getBus().getDirectionActuelle().getPointDepart());
                directionDTO.setPointArrivee(position.getBus().getDirectionActuelle().getPointArrivee());
                busDTO.setDirectionActuelle(directionDTO);
            }

            dto.setBus(busDTO);
        }

        return dto;
    }
}