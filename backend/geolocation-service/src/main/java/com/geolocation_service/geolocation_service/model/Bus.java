package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bus")
public class Bus {

    @Id
    private String idBus;

    private String immatriculation;
    private String modele;
    private String marque;
    private int capacite;
    private int annee;
    private String statut; // {EN_SERVICE, MAINTENANCE, HORS_SERVICE, EN_GARAGE}

    // Nouvelles relations pour la recherche
    @DBRef
    private LigneBus ligneActuelle;

    @DBRef
    private Direction directionActuelle;

    // Relations existantes
    @DBRef
    private List<PositionBus> positions;

    @DBRef
    private List<IncidentBus> incidents;

    @DBRef
    private List<HistoriquePosition> historiques;
}