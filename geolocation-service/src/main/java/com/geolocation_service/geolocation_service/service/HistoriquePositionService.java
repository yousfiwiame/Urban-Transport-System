package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.HistoriquePosition;
import com.geolocation_service.geolocation_service.repository.HistoriquePositionRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HistoriquePositionService {

    private final HistoriquePositionRepository historiqueRepo;

    public HistoriquePositionService(HistoriquePositionRepository historiqueRepo) {
        this.historiqueRepo = historiqueRepo;
    }

    public List<HistoriquePosition> getAllHistorique() {
        return historiqueRepo.findAll();
    }

    public List<HistoriquePosition> getHistoriqueByBus(String busId) {
        return historiqueRepo.findByBusIdBus(busId);
    }

    public HistoriquePosition saveHistorique(HistoriquePosition historique) {
        return historiqueRepo.save(historique);
    }
}
