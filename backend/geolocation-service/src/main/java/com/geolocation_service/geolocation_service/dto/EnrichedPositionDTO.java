package com.geolocation_service.geolocation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour une position enrichie avec les informations complètes du bus
 * provenant du schedule-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedPositionDTO {
    
    private String idPosition;
    private Long busId;
    private double latitude;
    private double longitude;
    private double altitude;
    private double precision;
    private double vitesse;
    private double direction;
    private LocalDateTime timestamp;
    
    // Informations complètes du bus depuis schedule-service
    private BusInfoDTO bus;
    
    /**
     * DTO imbriqué contenant les informations du bus depuis schedule-service
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusInfoDTO {
        private Long id;
        private String busNumber;
        private String licensePlate;
        private String manufacturer;
        private String model;
        private Integer year;
        private Integer capacity;
        private String status;
        private Integer seatingCapacity;
        private Integer standingCapacity;
        private Boolean hasWifi;
        private Boolean hasAirConditioning;
        private Boolean hasGPS;
        private String notes;
    }
}

