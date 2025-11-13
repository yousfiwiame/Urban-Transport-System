package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.Direction;
import com.geolocation_service.geolocation_service.repository.DirectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectionService {

    private final DirectionRepository directionRepository;

    public DirectionService(DirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    public List<Direction> getAllDirections() {
        return directionRepository.findAll();
    }

    public List<Direction> getDirectionsByLigne(String ligneId) {
        return directionRepository.findByLigneIdLigne(ligneId);
    }

    public Optional<Direction> getDirectionById(String idDirection) {
        return directionRepository.findById(idDirection);
    }

    public Direction createDirection(Direction direction) {
        return directionRepository.save(direction);
    }

    public Direction updateDirection(String idDirection, Direction directionDetails) {
        return directionRepository.findById(idDirection)
                .map(direction -> {
                    direction.setNomDirection(directionDetails.getNomDirection());
                    direction.setPointDepart(directionDetails.getPointDepart());
                    direction.setPointArrivee(directionDetails.getPointArrivee());
                    direction.setLigne(directionDetails.getLigne());
                    return directionRepository.save(direction);
                })
                .orElseThrow(() -> new RuntimeException("Direction non trouvée avec id: " + idDirection));
    }

    public void deleteDirection(String idDirection) {
        directionRepository.deleteById(idDirection);
    }
}