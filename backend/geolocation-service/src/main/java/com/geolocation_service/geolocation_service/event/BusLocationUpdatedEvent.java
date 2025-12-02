package com.geolocation_service.geolocation_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a bus location is updated.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusLocationUpdatedEvent {
    private String busId;
    private Double latitude;
    private Double longitude;
    private Double speed;
    private String currentRoute;
    private String nextStop;
    private LocalDateTime timestamp;
}
