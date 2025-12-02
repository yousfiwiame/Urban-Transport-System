package com.transport.urbain.controller;

import com.transport.urbain.dto.response.DriverDailyStatsResponse;
import com.transport.urbain.dto.response.DriverPerformanceResponse;
import com.transport.urbain.dto.response.DriverTripResponse;
import com.transport.urbain.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver", description = "Driver dashboard and tracking APIs")
@CrossOrigin(origins = "*")
public class DriverController {

    private final DriverService driverService;

    @GetMapping("/{driverId}/stats/today")
    @Operation(summary = "Get driver daily statistics")
    public ResponseEntity<DriverDailyStatsResponse> getTodayStats(@PathVariable Long driverId) {
        log.info("Fetching today's stats for driver: {}", driverId);
        DriverDailyStatsResponse stats = driverService.getTodayStats(driverId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{driverId}/trips/upcoming")
    @Operation(summary = "Get upcoming trips for a driver")
    public ResponseEntity<List<DriverTripResponse>> getUpcomingTrips(@PathVariable Long driverId) {
        log.info("Fetching upcoming trips for driver: {}", driverId);
        List<DriverTripResponse> trips = driverService.getUpcomingTrips(driverId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{driverId}/trips/completed/today")
    @Operation(summary = "Get completed trips today for a driver")
    public ResponseEntity<List<DriverTripResponse>> getCompletedTripsToday(@PathVariable Long driverId) {
        log.info("Fetching completed trips today for driver: {}", driverId);
        List<DriverTripResponse> trips = driverService.getCompletedTripsToday(driverId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{driverId}/performance")
    @Operation(summary = "Get driver performance metrics")
    public ResponseEntity<DriverPerformanceResponse> getPerformance(@PathVariable Long driverId) {
        log.info("Fetching performance metrics for driver: {}", driverId);
        DriverPerformanceResponse performance = driverService.getPerformance(driverId);
        return ResponseEntity.ok(performance);
    }

    @PostMapping("/{driverId}/trips/{tripId}/start")
    @Operation(summary = "Mark a trip as started")
    public ResponseEntity<DriverTripResponse> startTrip(
            @PathVariable Long driverId,
            @PathVariable Long tripId) {
        log.info("Starting trip {} for driver {}", tripId, driverId);
        DriverTripResponse trip = driverService.startTrip(driverId, tripId);
        return ResponseEntity.ok(trip);
    }

    @PostMapping("/{driverId}/trips/{tripId}/complete")
    @Operation(summary = "Mark a trip as completed")
    public ResponseEntity<DriverTripResponse> completeTrip(
            @PathVariable Long driverId,
            @PathVariable Long tripId,
            @RequestParam(required = false) Integer actualPassengers) {
        log.info("Completing trip {} for driver {} with {} passengers", tripId, driverId, actualPassengers);
        DriverTripResponse trip = driverService.completeTrip(driverId, tripId, actualPassengers);
        return ResponseEntity.ok(trip);
    }
}
