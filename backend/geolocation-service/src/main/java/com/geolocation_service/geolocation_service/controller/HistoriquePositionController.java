package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.model.HistoriquePosition;
import com.geolocation_service.geolocation_service.service.HistoriquePositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
public class HistoriquePositionController {

    private final HistoriquePositionService historiqueService;

    public HistoriquePositionController(HistoriquePositionService historiqueService) {
        this.historiqueService = historiqueService;
    }

    @GetMapping
    public List<HistoriquePosition> getAllHistorique() {
        return historiqueService.getAllHistorique();
    }

    @GetMapping("/bus/{busId}")
    public List<HistoriquePosition> getHistoriqueByBus(@PathVariable String busId) {
        return historiqueService.getHistoriqueByBus(busId);
    }

    @PostMapping
    public HistoriquePosition saveHistorique(@RequestBody HistoriquePosition historique) {
        return historiqueService.saveHistorique(historique);
    }
}
