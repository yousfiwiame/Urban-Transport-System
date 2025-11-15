package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.ZoneGeographique;
import com.geolocation_service.geolocation_service.repository.ZoneGeographiqueRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZoneGeographiqueService {

    private final ZoneGeographiqueRepository zoneRepo;

    public ZoneGeographiqueService(ZoneGeographiqueRepository zoneRepo) {
        this.zoneRepo = zoneRepo;
    }

    public List<ZoneGeographique> getAllZones() {
        return zoneRepo.findAll();
    }

    public ZoneGeographique createZone(ZoneGeographique zone) {
        return zoneRepo.save(zone);
    }

    public ZoneGeographique getZoneByNom(String nom) {
        return zoneRepo.findByNom(nom);
    }
}
