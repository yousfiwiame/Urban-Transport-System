package com.transport.urbain.service.impl;

import com.transport.urbain.dto.response.DriverDailyStatsResponse;
import com.transport.urbain.dto.response.DriverPerformanceResponse;
import com.transport.urbain.dto.response.DriverTripResponse;
import com.transport.urbain.model.User;
import com.transport.urbain.repository.UserRepository;
import com.transport.urbain.service.DriverService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String SCHEDULE_SERVICE_URL = "http://schedule-service";
    private static final String DRIVER_API_PATH = "/api/schedules/driver/";

    @Override
    public DriverDailyStatsResponse getTodayStats(Long driverId) {
        log.info("Fetching today's stats for driver: {}", driverId);

        // Verify driver exists
        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        try {
            String url = SCHEDULE_SERVICE_URL + DRIVER_API_PATH + driverId + "/stats/today";
            ResponseEntity<DriverStatsDTO> response = restTemplate.getForEntity(url, DriverStatsDTO.class);

            if (response.getBody() != null) {
                DriverStatsDTO stats = response.getBody();
                return DriverDailyStatsResponse.builder()
                        .tripsToday(stats.getTripsToday())
                        .drivingHours(stats.getDrivingHours())
                        .passengersTransported(stats.getPassengersTransported())
                        .busStatus(stats.getBusStatus())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error fetching stats from schedule-service: {}", e.getMessage());
        }

        // Return default if service unavailable
        return DriverDailyStatsResponse.builder()
                .tripsToday(0)
                .drivingHours(0.0)
                .passengersTransported(0)
                .busStatus("DISPONIBLE")
                .build();
    }

    @Override
    public List<DriverTripResponse> getUpcomingTrips(Long driverId) {
        log.info("Fetching upcoming trips for driver: {}", driverId);

        // Verify driver exists
        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        try {
            String url = SCHEDULE_SERVICE_URL + DRIVER_API_PATH + driverId + "/upcoming";
            ResponseEntity<List<ScheduleDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ScheduleDTO>>() {}
            );

            if (response.getBody() != null) {
                return response.getBody().stream()
                        .map(this::convertToDriverTripResponse)
                        .toList();
            }
        } catch (Exception e) {
            log.error("Error fetching upcoming trips from schedule-service: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    public List<DriverTripResponse> getCompletedTripsToday(Long driverId) {
        log.info("Fetching completed trips today for driver: {}", driverId);

        // Verify driver exists
        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        try {
            String url = SCHEDULE_SERVICE_URL + DRIVER_API_PATH + driverId + "/completed/today";
            ResponseEntity<List<ScheduleDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ScheduleDTO>>() {}
            );

            if (response.getBody() != null) {
                return response.getBody().stream()
                        .map(this::convertToDriverTripResponse)
                        .toList();
            }
        } catch (Exception e) {
            log.error("Error fetching completed trips from schedule-service: {}", e.getMessage());
        }

        return new ArrayList<>();
    }

    @Override
    public DriverPerformanceResponse getPerformance(Long driverId) {
        log.info("Fetching performance metrics for driver: {}", driverId);

        // Verify driver exists
        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // TODO: Make REST call to schedule-service and possibly a rating service
        // to calculate these metrics

        return DriverPerformanceResponse.builder()
                .punctuality(0.0)  // TODO: Calculate from trip data
                .averageRating(0.0)  // TODO: Fetch from rating system
                .totalTripsThisMonth(0)  // TODO: Count from schedule-service
                .totalTripsLastMonth(0)  // TODO: Count from schedule-service
                .completionRate(0.0)  // TODO: Calculate from trip data
                .customerSatisfaction(0.0)  // TODO: Fetch from rating system
                .build();
    }

    @Override
    public DriverTripResponse startTrip(Long driverId, Long tripId) {
        log.info("Starting trip {} for driver {}", tripId, driverId);

        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        try {
            String url = SCHEDULE_SERVICE_URL + "/api/schedules/" + tripId + "/start";
            ResponseEntity<ScheduleDTO> response = restTemplate.postForEntity(url, null, ScheduleDTO.class);

            if (response.getBody() != null) {
                ScheduleDTO schedule = response.getBody();
                return DriverTripResponse.builder()
                        .id(schedule.getId())
                        .scheduleId(schedule.getId())
                        .status("IN_PROGRESS")
                        .departure(schedule.getDepartureTime() != null ? schedule.getDepartureTime().toString() : null)
                        .arrival(schedule.getArrivalTime() != null ? schedule.getArrivalTime().toString() : null)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error starting trip: {}", e.getMessage());
        }

        return DriverTripResponse.builder()
                .id(tripId)
                .status("IN_PROGRESS")
                .build();
    }

    @Override
    public DriverTripResponse completeTrip(Long driverId, Long tripId, Integer actualPassengers) {
        log.info("Completing trip {} for driver {} with {} passengers", tripId, driverId, actualPassengers);

        userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        try {
            String url = SCHEDULE_SERVICE_URL + "/api/schedules/" + tripId + "/complete";
            Map<String, Integer> requestBody = Map.of("actualPassengers", actualPassengers);
            ResponseEntity<ScheduleDTO> response = restTemplate.postForEntity(url, requestBody, ScheduleDTO.class);

            if (response.getBody() != null) {
                ScheduleDTO schedule = response.getBody();
                return DriverTripResponse.builder()
                        .id(schedule.getId())
                        .scheduleId(schedule.getId())
                        .status("COMPLETED")
                        .actualPassengers(actualPassengers)
                        .departure(schedule.getDepartureTime() != null ? schedule.getDepartureTime().toString() : null)
                        .arrival(schedule.getArrivalTime() != null ? schedule.getArrivalTime().toString() : null)
                        .build();
            }
        } catch (Exception e) {
            log.error("Error completing trip: {}", e.getMessage());
        }

        return DriverTripResponse.builder()
                .id(tripId)
                .status("COMPLETED")
                .actualPassengers(actualPassengers)
                .build();
    }

    // Helper method to convert ScheduleDTO to DriverTripResponse
    private DriverTripResponse convertToDriverTripResponse(ScheduleDTO schedule) {
        return DriverTripResponse.builder()
                .id(schedule.getId())
                .scheduleId(schedule.getId())
                .departure(schedule.getDepartureTime() != null ? schedule.getDepartureTime().toString() : null)
                .arrival(schedule.getArrivalTime() != null ? schedule.getArrivalTime().toString() : null)
                .route(schedule.getRouteNumber())
                .routeName(schedule.getRouteName())
                .routeId(schedule.getRouteId())
                .status("SCHEDULED")
                .departureTime(schedule.getDepartureTime() != null ? schedule.getDepartureTime().toString() : null)
                .arrivalTime(schedule.getArrivalTime() != null ? schedule.getArrivalTime().toString() : null)
                .build();
    }

    // DTOs for schedule-service communication
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private Long id;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private Long busId;
        private LocalTime departureTime;
        private LocalTime arrivalTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DriverStatsDTO {
        private Integer tripsToday;
        private Integer completedTrips;
        private Double drivingHours;
        private Integer passengersTransported;
        private String busStatus;
    }
}
