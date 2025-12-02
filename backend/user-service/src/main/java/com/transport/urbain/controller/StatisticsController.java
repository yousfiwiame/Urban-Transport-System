package com.transport.urbain.controller;

import com.transport.urbain.repository.UserRepository;
import com.transport.urbain.model.RoleName;
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
 * REST controller for statistics endpoints.
 * Provides aggregated statistics for the admin dashboard.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics", description = "Statistics endpoints for admin dashboard")
@SecurityRequirement(name = "Bearer Authentication")
public class StatisticsController {

    private final UserRepository userRepository;

    /**
     * Get user statistics for the admin dashboard.
     * 
     * Returns:
     * - Total number of users
     * - Number of users by role (passengers, drivers, admins)
     * - Number of active/inactive users
     * - Number of verified users
     * 
     * @return ResponseEntity containing user statistics
     */
    @GetMapping("/users")
    @Operation(summary = "Get user statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        log.info("Fetching user statistics for admin dashboard");

        Map<String, Object> stats = new HashMap<>();

        // Total users
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // Users by role
        long totalPassengers = userRepository.countUsersByRole(RoleName.PASSENGER);
        long totalDrivers = userRepository.countUsersByRole(RoleName.DRIVER);
        long totalAdmins = userRepository.countUsersByRole(RoleName.ADMIN);

        stats.put("passengers", totalPassengers);
        stats.put("drivers", totalDrivers);
        stats.put("admins", totalAdmins);

        // Active vs inactive users
        long activeUsers = userRepository.countByEnabledTrue();
        long inactiveUsers = totalUsers - activeUsers;

        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);

        // Verified users
        long emailVerifiedUsers = userRepository.countByEmailVerifiedTrue();
        long phoneVerifiedUsers = userRepository.countByPhoneVerifiedTrue();

        stats.put("emailVerifiedUsers", emailVerifiedUsers);
        stats.put("phoneVerifiedUsers", phoneVerifiedUsers);

        log.info("User statistics: {} total users, {} passengers, {} drivers, {} admins",
                totalUsers, totalPassengers, totalDrivers, totalAdmins);

        return ResponseEntity.ok(stats);
    }
}

