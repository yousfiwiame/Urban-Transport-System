package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionBusService {

    private final PositionBusRepository positionBusRepository;

    public PositionBusService(PositionBusRepository positionBusRepository) {
        this.positionBusRepository = positionBusRepository;
    }

    public List<PositionBus> getAllPositions() {
        return positionBusRepository.findAll();
    }

    public List<PositionBus> getPositionsByBusId(Long busId) {
        return positionBusRepository.findByBusId(busId);
    }

    public PositionBus addPosition(PositionBus positionBus) {
        // Ensure timestamp is set if not provided
        if (positionBus.getTimestamp() == null) {
            positionBus.setTimestamp(LocalDateTime.now());
        }
        return positionBusRepository.save(positionBus);
    }

    /**
     * Get the latest position for a specific bus
     */
    public PositionBus getLatestPosition(Long busId) {
        List<PositionBus> positions = positionBusRepository.findByBusId(busId);
        return positions.isEmpty() ? null : positions.get(positions.size() - 1);
    }

    /**
     * Get all active bus positions (updated in last 5 minutes)
     */
    public List<PositionBus> getAllActiveBusPositions() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<PositionBus> allPositions = positionBusRepository.findAll();

        return allPositions.stream()
                .filter(pos -> pos.getTimestamp() != null && pos.getTimestamp().isAfter(fiveMinutesAgo))
                .collect(Collectors.toList());
    }

    /**
     * Get all unique bus IDs that have active positions
     */
    public List<Long> getActiveBusIds() {
        return getAllActiveBusPositions().stream()
                .map(PositionBus::getBusId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
