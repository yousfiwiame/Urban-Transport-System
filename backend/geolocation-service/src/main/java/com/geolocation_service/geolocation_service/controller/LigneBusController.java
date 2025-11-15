package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.LigneBusDTO;
import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.service.LigneBusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lignes")
public class LigneBusController {

    private final LigneBusService ligneBusService;

    public LigneBusController(LigneBusService ligneBusService) {
        this.ligneBusService = ligneBusService;
    }

    @GetMapping
    public List<LigneBusDTO> getAllLignes() {
        return ligneBusService.getAllLignes().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/actives")
    public List<LigneBusDTO> getLignesActives() {
        return ligneBusService.getLignesActives().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public LigneBusDTO getLigneById(@PathVariable String id) {
        LigneBus ligne = ligneBusService.getLigneById(id)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée"));
        return convertToDTO(ligne);
    }

    @GetMapping("/numero/{numero}")
    public LigneBusDTO getLigneByNumero(@PathVariable String numero) {
        LigneBus ligne = ligneBusService.getLigneByNumero(numero)
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée"));
        return convertToDTO(ligne);
    }

    @PostMapping
    public LigneBus createLigne(@RequestBody LigneBus ligne) {
        return ligneBusService.createLigne(ligne);
    }

    @PutMapping("/{id}")
    public LigneBus updateLigne(@PathVariable String id, @RequestBody LigneBus ligne) {
        return ligneBusService.updateLigne(id, ligne);
    }

    @DeleteMapping("/{id}")
    public void deleteLigne(@PathVariable String id) {
        ligneBusService.deleteLigne(id);
    }

    private LigneBusDTO convertToDTO(LigneBus ligne) {
        LigneBusDTO dto = new LigneBusDTO();
        dto.setIdLigne(ligne.getIdLigne());
        dto.setNumeroLigne(ligne.getNumeroLigne());
        dto.setNomLigne(ligne.getNomLigne());
        dto.setCouleur(ligne.getCouleur());
        return dto;
    }
}