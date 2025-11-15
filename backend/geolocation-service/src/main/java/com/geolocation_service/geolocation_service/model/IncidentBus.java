package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_bus")
public class IncidentBus {

    @Id
    private String idIncident;

    private String type;       // {PANNE, ACCIDENT, RETARD, EMBOUITEILLAGE}
    private String description;
    private String gravite;    // {FAIBLE, MOYENNE, HAUTE, CRITIQUE}
    private LocalDateTime dateSignalement;
    private String statut;     // {SIGNALE, EN_COURS, RESOLU}
    private double latitude;
    private double longitude;

    @DBRef
    private Bus bus;
}
