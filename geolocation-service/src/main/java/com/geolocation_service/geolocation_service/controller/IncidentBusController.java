package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.model.IncidentBus;
import com.geolocation_service.geolocation_service.service.IncidentBusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentBusController {

    private final IncidentBusService incidentService;

    public IncidentBusController(IncidentBusService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<IncidentBus> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/bus/{busId}")
    public List<IncidentBus> getIncidentsByBus(@PathVariable String busId) {
        return incidentService.getIncidentsByBus(busId);
    }

    @GetMapping("/statut/{statut}")
    public List<IncidentBus> getIncidentsByStatut(@PathVariable String statut) {
        return incidentService.getIncidentsByStatut(statut);
    }

    @PostMapping
    public IncidentBus createIncident(@RequestBody IncidentBus incident) {
        return incidentService.saveIncident(incident);
    }
}
