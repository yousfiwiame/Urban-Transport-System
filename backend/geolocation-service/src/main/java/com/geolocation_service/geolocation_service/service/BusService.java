package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.repository.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Optional<Bus> getBusById(String idBus) {
        return busRepository.findById(idBus);
    }

    public List<Bus> getBusesByLigne(String ligneId) {
        return busRepository.findByLigneActuelleIdLigne(ligneId);
    }

    public List<Bus> getBusesByDirection(String directionId) {
        return busRepository.findByDirectionActuelleIdDirection(directionId);
    }

    public List<Bus> getBusesByLigneAndDirection(String ligneId, String directionId) {
        return busRepository.findByLigneActuelleIdLigneAndDirectionActuelleIdDirection(ligneId, directionId);
    }

    public Bus createBus(Bus bus) {
        return busRepository.save(bus);
    }

    public Bus updateBus(String idBus, Bus busDetails) {
        return busRepository.findById(idBus)
                .map(bus -> {
                    bus.setImmatriculation(busDetails.getImmatriculation());
                    bus.setModele(busDetails.getModele());
                    bus.setMarque(busDetails.getMarque());
                    bus.setCapacite(busDetails.getCapacite());
                    bus.setAnnee(busDetails.getAnnee());
                    bus.setStatut(busDetails.getStatut());
                    bus.setLigneActuelle(busDetails.getLigneActuelle());
                    bus.setDirectionActuelle(busDetails.getDirectionActuelle());
                    return busRepository.save(bus);
                })
                .orElseThrow(() -> new RuntimeException("Bus non trouvé avec id: " + idBus));
    }

    public void deleteBus(String idBus) {
        busRepository.deleteById(idBus);
    }
}