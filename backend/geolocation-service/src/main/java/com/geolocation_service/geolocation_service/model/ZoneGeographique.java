package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "zone_geographique")
public class ZoneGeographique {

    @Id
    private String idZone;

    private String nom;
    private String description;
    private boolean actif;
}
