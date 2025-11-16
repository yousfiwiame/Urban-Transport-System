package com.geolocation_service.geolocation_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusDTO {
    private String idBus;
    private String immatriculation;
    private String modele;
    private String marque;
    private int capacite;
    private int annee;
    private String statut;
    private LigneBusDTO ligneActuelle;
    private DirectionDTO directionActuelle;
}