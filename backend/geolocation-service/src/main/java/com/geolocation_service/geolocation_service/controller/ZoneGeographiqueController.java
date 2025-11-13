package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.model.ZoneGeographique;
import com.geolocation_service.geolocation_service.service.ZoneGeographiqueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
public class ZoneGeographiqueController {

    private final ZoneGeographiqueService zoneService;

    public ZoneGeographiqueController(ZoneGeographiqueService zoneService) {
        this.zoneService = zoneService;
    }

    @GetMapping
    public List<ZoneGeographique> getAllZones() {
        return zoneService.getAllZones();
    }

    @GetMapping("/{nom}")
    public ZoneGeographique getZoneByNom(@PathVariable String nom) {
        return zoneService.getZoneByNom(nom);
    }

    @PostMapping
    public ZoneGeographique createZone(@RequestBody ZoneGeographique zone) {
        return zoneService.createZone(zone);
    }
}
