package com.geolocation_service.geolocation_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneBusDTO {
    private String idLigne;
    private String numeroLigne;
    private String nomLigne;
    private String couleur;
}