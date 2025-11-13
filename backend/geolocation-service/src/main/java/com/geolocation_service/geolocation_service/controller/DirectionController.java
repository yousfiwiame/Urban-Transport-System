package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.DirectionDTO;
import com.geolocation_service.geolocation_service.model.Direction;
import com.geolocation_service.geolocation_service.service.DirectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/directions")
public class DirectionController {

    private final DirectionService directionService;

    public DirectionController(DirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping
    public List<DirectionDTO> getAllDirections() {
        return directionService.getAllDirections().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/ligne/{ligneId}")
    public List<DirectionDTO> getDirectionsByLigne(@PathVariable String ligneId) {
        return directionService.getDirectionsByLigne(ligneId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public DirectionDTO getDirectionById(@PathVariable String id) {
        Direction direction = directionService.getDirectionById(id)
                .orElseThrow(() -> new RuntimeException("Direction non trouvée"));
        return convertToDTO(direction);
    }

    @PostMapping
    public Direction createDirection(@RequestBody Direction direction) {
        return directionService.createDirection(direction);
    }

    @PutMapping("/{id}")
    public Direction updateDirection(@PathVariable String id, @RequestBody Direction direction) {
        return directionService.updateDirection(id, direction);
    }

    @DeleteMapping("/{id}")
    public void deleteDirection(@PathVariable String id) {
        directionService.deleteDirection(id);
    }

    private DirectionDTO convertToDTO(Direction direction) {
        DirectionDTO dto = new DirectionDTO();
        dto.setIdDirection(direction.getIdDirection());
        dto.setNomDirection(direction.getNomDirection());
        dto.setPointDepart(direction.getPointDepart());
        dto.setPointArrivee(direction.getPointArrivee());
        return dto;
    }
}