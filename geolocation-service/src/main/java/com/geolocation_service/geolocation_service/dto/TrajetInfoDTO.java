package com.geolocation_service.geolocation_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrajetInfoDTO {
    private String idBus;
    private String immatriculation;
    private LigneBusDTO ligne;
    private DirectionDTO direction;

    // Informations de position actuelle
    private double latitudeActuelle;
    private double longitudeActuelle;
    private double vitesseActuelle;
    private LocalDateTime derniereMiseAJour;

    // Statistiques du trajet en cours
    private LocalDateTime heureDepart;
    private double distanceParcourue;
    private int dureeTrajetMinutes;
    private int nombreArretsEffectues;

    // Prochaines informations
    private String prochainArret;
    private double distanceProchainArret;
    private int tempsEstimeProchainArret; // en minutes
}