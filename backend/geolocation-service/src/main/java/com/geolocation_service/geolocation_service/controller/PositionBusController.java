package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.*;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.service.PositionBusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/positions")
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
    public List<PositionBusDTO> getPositionsByBus(@PathVariable String busId) {
        return positionBusService.getPositionsByBusId(busId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public PositionBus addPosition(@RequestBody PositionBus positionBus) {
        return positionBusService.addPosition(positionBus);
    }

    private PositionBusDTO convertToDTO(PositionBus position) {
        PositionBusDTO dto = new PositionBusDTO();
        dto.setIdPosition(position.getIdPosition());
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