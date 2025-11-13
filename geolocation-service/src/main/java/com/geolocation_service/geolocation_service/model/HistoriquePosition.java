package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "historique_position")
public class HistoriquePosition {

    @Id
    private String idHistorique;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private double distanceParcourue;
    private int dureeTrajet;
    private int nombreArrets;

    @DBRef
    private Bus bus;
}
