package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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
        return positionBusRepository.save(positionBus);
    }
}
