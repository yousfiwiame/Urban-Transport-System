package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.IncidentBus;
import com.geolocation_service.geolocation_service.repository.IncidentBusRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IncidentBusService {

    private final IncidentBusRepository incidentRepo;

    public IncidentBusService(IncidentBusRepository incidentRepo) {
        this.incidentRepo = incidentRepo;
    }

    public List<IncidentBus> getAllIncidents() {
        return incidentRepo.findAll();
    }

    public List<IncidentBus> getIncidentsByBus(String busId) {
        return incidentRepo.findByBusIdBus(busId);
    }

    public List<IncidentBus> getIncidentsByStatut(String statut) {
        return incidentRepo.findByStatut(statut);
    }

    public IncidentBus saveIncident(IncidentBus incident) {
        return incidentRepo.save(incident);
    }
}
