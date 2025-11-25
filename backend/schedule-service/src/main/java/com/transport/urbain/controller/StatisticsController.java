package com.transport.urbain.controller;

import com.transport.urbain.repository.BusRepository;
import com.transport.urbain.repository.RouteRepository;
import com.transport.urbain.repository.ScheduleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for schedule service statistics.
 * Provides statistics for buses, routes, and schedules.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "Statistics endpoints for admin dashboard")
@SecurityRequirement(name = "Bearer Authentication")
public class StatisticsController {

    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * Get bus and route statistics for the admin dashboard.
     * 
     * Returns:
     * - Total number of buses
     * - Number of active buses
     * - Number of available/in-service/maintenance buses
     * - Total number of routes
     * - Number of active routes
     * - Total number of schedules
     * 
     * @return ResponseEntity containing schedule service statistics
     */
    @GetMapping("/schedules")
    @Operation(summary = "Get schedule service statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<Map<String, Object>> getScheduleStatistics() {
        log.info("Fetching schedule statistics for admin dashboard");

        Map<String, Object> stats = new HashMap<>();

        // Bus statistics
        long totalBuses = busRepository.count();
        stats.put("totalBuses", totalBuses);

        long activeBuses = busRepository.countByActiveTrue();
        stats.put("activeBuses", activeBuses);

        // Route statistics
        long totalRoutes = routeRepository.count();
        stats.put("totalRoutes", totalRoutes);

        long activeRoutes = routeRepository.countByActiveTrue();
        stats.put("activeRoutes", activeRoutes);

        // Schedule statistics
        long totalSchedules = scheduleRepository.count();
        stats.put("totalSchedules", totalSchedules);

        long activeSchedules = scheduleRepository.countByActiveTrue();
        stats.put("activeSchedules", activeSchedules);

        log.info("Schedule statistics: {} buses, {} routes, {} schedules",
                totalBuses, totalRoutes, totalSchedules);

        return ResponseEntity.ok(stats);
    }
}

