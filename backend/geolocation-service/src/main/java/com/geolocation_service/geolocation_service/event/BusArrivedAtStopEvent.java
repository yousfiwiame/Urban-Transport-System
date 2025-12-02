package com.geolocation_service.geolocation_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a bus arrives at a stop.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusArrivedAtStopEvent {
    private String busId;
    private String stopId;
    private String stopName;
    private String routeName;
    private LocalDateTime arrivedAt;
    private Integer expectedDelayMinutes;
}
