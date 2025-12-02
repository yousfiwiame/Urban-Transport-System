package com.transport.urbain.service;

import com.transport.urbain.dto.response.DriverDailyStatsResponse;
import com.transport.urbain.dto.response.DriverPerformanceResponse;
import com.transport.urbain.dto.response.DriverTripResponse;

import java.util.List;

public interface DriverService {

    DriverDailyStatsResponse getTodayStats(Long driverId);

    List<DriverTripResponse> getUpcomingTrips(Long driverId);

    List<DriverTripResponse> getCompletedTripsToday(Long driverId);

    DriverPerformanceResponse getPerformance(Long driverId);

    DriverTripResponse startTrip(Long driverId, Long tripId);

    DriverTripResponse completeTrip(Long driverId, Long tripId, Integer actualPassengers);
}
