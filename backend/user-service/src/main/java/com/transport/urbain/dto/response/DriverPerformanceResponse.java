package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverPerformanceResponse {

    private Double punctuality; // Percentage of on-time trips
    private Double averageRating; // Average rating out of 5
    private Integer totalTripsThisMonth;
    private Integer totalTripsLastMonth;
    private Double completionRate; // Percentage of completed trips
    private Double customerSatisfaction; // Customer satisfaction score (0-5)
}
