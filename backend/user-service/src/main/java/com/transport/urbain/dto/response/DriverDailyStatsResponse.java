package com.transport.urbain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDailyStatsResponse {

    private Integer tripsToday;
    private Double drivingHours;
    private Integer passengersTransported;
    private String busStatus;
    private String busId;
    private String busNumber;
}
