package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.BusDTO;
import com.geolocation_service.geolocation_service.dto.DirectionDTO;
import com.geolocation_service.geolocation_service.dto.LigneBusDTO;
import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.service.BusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bus")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public List<BusDTO> getAllBuses() {
        return busService.getAllBuses().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BusDTO getBusById(@PathVariable String id) {
        Bus bus = busService.getBusById(id)
                .orElseThrow(() -> new RuntimeException("Bus non trouvé"));
        return convertToDTO(bus);
    }

    @GetMapping("/ligne/{ligneId}")
    public List<BusDTO> getBusesByLigne(@PathVariable String ligneId) {
        return busService.getBusesByLigne(ligneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/direction/{directionId}")
    public List<BusDTO> getBusesByDirection(@PathVariable String directionId) {
        return busService.getBusesByDirection(directionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<BusDTO> searchBuses(
            @RequestParam(required = false) String ligneId,
            @RequestParam(required = false) String directionId
    ) {
        List<Bus> buses;
        if (ligneId != null && directionId != null) {
            buses = busService.getBusesByLigneAndDirection(ligneId, directionId);
        } else if (ligneId != null) {
            buses = busService.getBusesByLigne(ligneId);
        } else if (directionId != null) {
            buses = busService.getBusesByDirection(directionId);
        } else {
            buses = busService.getAllBuses();
        }

        return buses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public Bus createBus(@RequestBody Bus bus) {
        return busService.createBus(bus);
    }

    @PutMapping("/{id}")
    public Bus updateBus(@PathVariable String id, @RequestBody Bus bus) {
        return busService.updateBus(id, bus);
    }

    @DeleteMapping("/{id}")
    public void deleteBus(@PathVariable String id) {
        busService.deleteBus(id);
    }

    private BusDTO convertToDTO(Bus bus) {
        BusDTO dto = new BusDTO();
        dto.setIdBus(bus.getIdBus());
        dto.setImmatriculation(bus.getImmatriculation());
        dto.setModele(bus.getModele());
        dto.setMarque(bus.getMarque());
        dto.setCapacite(bus.getCapacite());
        dto.setAnnee(bus.getAnnee());
        dto.setStatut(bus.getStatut());

        if (bus.getLigneActuelle() != null) {
            LigneBusDTO ligneDTO = new LigneBusDTO();
            ligneDTO.setIdLigne(bus.getLigneActuelle().getIdLigne());
            ligneDTO.setNumeroLigne(bus.getLigneActuelle().getNumeroLigne());
            ligneDTO.setNomLigne(bus.getLigneActuelle().getNomLigne());
            ligneDTO.setCouleur(bus.getLigneActuelle().getCouleur());
            dto.setLigneActuelle(ligneDTO);
        }

        if (bus.getDirectionActuelle() != null) {
            DirectionDTO directionDTO = new DirectionDTO();
            directionDTO.setIdDirection(bus.getDirectionActuelle().getIdDirection());
            directionDTO.setNomDirection(bus.getDirectionActuelle().getNomDirection());
            directionDTO.setPointDepart(bus.getDirectionActuelle().getPointDepart());
            directionDTO.setPointArrivee(bus.getDirectionActuelle().getPointArrivee());
            dto.setDirectionActuelle(directionDTO);
        }

        return dto;
    }
}