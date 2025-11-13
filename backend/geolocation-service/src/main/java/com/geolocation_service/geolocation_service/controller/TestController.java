package com.geolocation_service.geolocation_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/hello")
    public String hello() {
        return "Backend Spring Boot connecté avec succès 🚀";
    }
}
