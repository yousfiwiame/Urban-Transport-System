package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "position_bus")
@JsonIgnoreProperties({"bus.positions", "bus.incidents", "bus.historiques"}) // éviter boucle infinie

public class PositionBus {

    @Id
    private String idPosition;

    private double latitude;
    private double longitude;
    private double altitude;
    private double precision;
    private double vitesse;
    private double direction;
    private LocalDateTime timestamp;

    /**
     * Référence au bus dans schedule-service (nouveau système)
     * Ce champ contient l'ID du bus dans PostgreSQL
     */
    private Long busId;

    /**
     * Référence au bus dans MongoDB (ancien système)
     * @deprecated Utilisez busId à la place
     */
    @Deprecated
    @DBRef
    private Bus bus;

    @DBRef
    private ZoneGeographique zone;
}
