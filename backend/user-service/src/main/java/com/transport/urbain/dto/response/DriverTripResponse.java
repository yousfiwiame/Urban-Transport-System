package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverTripResponse {

    private Long id;
    private Long scheduleId;
    private String departure;
    private String arrival;
    private String route;
    private String routeName;
    private Long routeId;
    private Integer estimatedPassengers;
    private Integer actualPassengers;
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    private String departureTime;
    private String arrivalTime;
}
