package com.geolocation_service.geolocation_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "direction")
public class Direction {

    @Id
    private String idDirection;

    private String nomDirection;
    private String pointDepart;
    private String pointArrivee;

    @DBRef
    private LigneBus ligne;
}