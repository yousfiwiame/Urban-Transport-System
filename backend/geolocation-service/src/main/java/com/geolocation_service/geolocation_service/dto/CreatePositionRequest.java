package com.geolocation_service.geolocation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour créer une nouvelle position GPS depuis l'application conducteur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePositionRequest {
    
    private Long busId;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Double precision;
    private Double vitesse;
    private Double direction;
}

