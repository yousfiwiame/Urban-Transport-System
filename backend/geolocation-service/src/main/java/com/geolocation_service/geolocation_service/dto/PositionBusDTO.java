package com.geolocation_service.geolocation_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PositionBusDTO {
    private String idPosition;
    private Long busId;
    private double latitude;
    private double longitude;
    private double altitude;
    private double precision;
    private double vitesse;
    private double direction;
    private LocalDateTime timestamp;
    private BusDTO bus;
}