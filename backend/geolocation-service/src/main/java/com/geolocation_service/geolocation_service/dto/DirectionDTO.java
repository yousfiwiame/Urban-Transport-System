package com.geolocation_service.geolocation_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DirectionDTO {
    private String idDirection;
    private String nomDirection;
    private String pointDepart;
    private String pointArrivee;
}