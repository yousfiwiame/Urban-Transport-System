package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.service.PositionBusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * WebSocket controller for real-time bus location updates
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
@CrossOrigin(origins = "*")
public class BusLocationWebSocketController {

    private final PositionBusService positionBusService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle subscription to specific bus location updates
     */
    @MessageMapping("/bus/{busId}")
    @SendTo("/topic/bus/{busId}")
    public PositionBus getBusLocation(@DestinationVariable Long busId) {
        log.info("Client subscribed to bus location updates for bus: {}", busId);
        return positionBusService.getLatestPosition(busId);
    }

    /**
     * Broadcast all active bus locations every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    public void broadcastAllBusLocations() {
        try {
            List<PositionBus> activeLocations = positionBusService.getAllActiveBusPositions();

            if (!activeLocations.isEmpty()) {
                log.debug("Broadcasting {} active bus locations", activeLocations.size());
                messagingTemplate.convertAndSend("/topic/buses/all", activeLocations);
            }
        } catch (Exception e) {
            log.error("Error broadcasting bus locations: {}", e.getMessage());
        }
    }

    /**
     * Broadcast location updates for specific buses every 5 seconds
     */
    @Scheduled(fixedRate = 5000)
    public void broadcastIndividualBusLocations() {
        try {
            // Get all unique bus IDs with active positions
            List<Long> activeBusIds = positionBusService.getActiveBusIds();

            for (Long busId : activeBusIds) {
                PositionBus position = positionBusService.getLatestPosition(busId);
                if (position != null) {
                    messagingTemplate.convertAndSend("/topic/bus/" + busId, position);
                }
            }
        } catch (Exception e) {
            log.error("Error broadcasting individual bus locations: {}", e.getMessage());
        }
    }
}
