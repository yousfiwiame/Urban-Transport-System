package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.TrajetInfoDTO;
import com.geolocation_service.geolocation_service.service.TrajetInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trajet")
@CrossOrigin(origins = "*")  // ← Important pour éviter les erreurs CORS
public class TrajetInfoController {

    private final TrajetInfoService trajetInfoService;

    public TrajetInfoController(TrajetInfoService trajetInfoService) {
        this.trajetInfoService = trajetInfoService;
    }

    @GetMapping("/bus/{busId}")
    public TrajetInfoDTO getTrajetInfo(@PathVariable String busId) {
        return trajetInfoService.getTrajetInfo(busId);
    }
}