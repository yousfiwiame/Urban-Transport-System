package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ligne_bus")
public class LigneBus {

    @Id
    private String idLigne;

    private String numeroLigne;
    private String nomLigne;
    private String description;
    private String couleur; // Code hexadécimal pour l'affichage
    private boolean actif;
}