package com.transport.urbain.controller;

import com.transport.urbain.dto.response.ScheduleResponse;
import com.transport.urbain.service.ScheduleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for driver-specific schedule operations
 * Provides schedule data for driver dashboards (driver assignment managed in user-service)
 */
@RestController
@RequestMapping("/api/schedules/driver")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DriverScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Get upcoming schedules
     * GET /api/schedules/driver/{driverId}/upcoming
     */
    @GetMapping("/{driverId}/upcoming")
    public ResponseEntity<List<ScheduleResponse>> getUpcomingSchedules(@PathVariable Long driverId) {
        log.info("Fetching upcoming schedules for driver: {}", driverId);

        try {
            LocalTime now = LocalTime.now();
            Page<ScheduleResponse> schedulesPage = scheduleService.getActiveSchedules(PageRequest.of(0, 50));

            List<ScheduleResponse> upcomingSchedules = schedulesPage.getContent().stream()
                    .filter(schedule -> schedule.getDepartureTime().isAfter(now))
                    .limit(10)
                    .collect(Collectors.toList());

            log.info("Found {} upcoming schedules", upcomingSchedules.size());
            return ResponseEntity.ok(upcomingSchedules);

        } catch (Exception e) {
            log.error("Error fetching upcoming schedules: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get completed schedules today
     * GET /api/schedules/driver/{driverId}/completed/today
     */
    @GetMapping("/{driverId}/completed/today")
    public ResponseEntity<List<ScheduleResponse>> getCompletedSchedulesToday(@PathVariable Long driverId) {
        log.info("Fetching completed schedules today for driver: {}", driverId);

        try {
            LocalTime now = LocalTime.now();
            Page<ScheduleResponse> schedulesPage = scheduleService.getActiveSchedules(PageRequest.of(0, 100));

            List<ScheduleResponse> completedSchedules = schedulesPage.getContent().stream()
                    .filter(schedule -> schedule.getDepartureTime().isBefore(now))
                    .collect(Collectors.toList());

            log.info("Found {} completed schedules", completedSchedules.size());
            return ResponseEntity.ok(completedSchedules);

        } catch (Exception e) {
            log.error("Error fetching completed schedules: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get today's statistics
     * GET /api/schedules/driver/{driverId}/stats/today
     */
    @GetMapping("/{driverId}/stats/today")
    public ResponseEntity<DriverDailyStatsDTO> getTodayStats(@PathVariable Long driverId) {
        log.info("Fetching today's stats for driver: {}", driverId);

        try {
            LocalTime now = LocalTime.now();
            Page<ScheduleResponse> schedulesPage = scheduleService.getActiveSchedules(PageRequest.of(0, 100));
            List<ScheduleResponse> todaySchedules = schedulesPage.getContent();

            int tripsToday = todaySchedules.size();
            long completedTrips = todaySchedules.stream()
                    .filter(schedule -> schedule.getDepartureTime().isBefore(now))
                    .count();

            double drivingHours = todaySchedules.stream()
                    .filter(schedule -> schedule.getDepartureTime().isBefore(now))
                    .mapToDouble(schedule -> {
                        long minutes = java.time.Duration.between(
                                schedule.getDepartureTime(),
                                schedule.getArrivalTime()
                        ).toMinutes();
                        return minutes / 60.0;
                    })
                    .sum();

            DriverDailyStatsDTO stats = DriverDailyStatsDTO.builder()
                    .tripsToday(tripsToday)
                    .completedTrips((int) completedTrips)
                    .drivingHours(Math.round(drivingHours * 10.0) / 10.0)
                    .passengersTransported(0)
                    .busStatus("ACTIF")
                    .build();

            log.info("Driver stats: {} trips, {} completed, {} hours", tripsToday, completedTrips, drivingHours);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Error fetching stats: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Start a schedule/trip
     * POST /api/schedules/{scheduleId}/start
     */
    @PostMapping("/{scheduleId}/start")
    public ResponseEntity<ScheduleResponse> startSchedule(@PathVariable Long scheduleId) {
        log.info("Starting schedule: {}", scheduleId);

        try {
            ScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
            log.info("Schedule {} started", scheduleId);
            return ResponseEntity.ok(schedule);

        } catch (Exception e) {
            log.error("Error starting schedule {}: {}", scheduleId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Complete a schedule/trip
     * POST /api/schedules/{scheduleId}/complete
     */
    @PostMapping("/{scheduleId}/complete")
    public ResponseEntity<ScheduleResponse> completeSchedule(
            @PathVariable Long scheduleId,
            @RequestBody(required = false) CompleteTripRequest request) {
        log.info("Completing schedule: {}", scheduleId);

        try {
            ScheduleResponse schedule = scheduleService.getScheduleById(scheduleId);
            log.info("Schedule {} completed", scheduleId);
            return ResponseEntity.ok(schedule);

        } catch (Exception e) {
            log.error("Error completing schedule {}: {}", scheduleId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @lombok.Builder
    public static class DriverDailyStatsDTO {
        private Integer tripsToday;
        private Integer completedTrips;
        private Double drivingHours;
        private Integer passengersTransported;
        private String busStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteTripRequest {
        private Integer actualPassengers;
    }
}
