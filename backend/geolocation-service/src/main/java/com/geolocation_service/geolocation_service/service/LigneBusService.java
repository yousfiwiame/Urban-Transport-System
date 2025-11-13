package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.repository.LigneBusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LigneBusService {

    private final LigneBusRepository ligneBusRepository;

    public LigneBusService(LigneBusRepository ligneBusRepository) {
        this.ligneBusRepository = ligneBusRepository;
    }

    public List<LigneBus> getAllLignes() {
        return ligneBusRepository.findAll();
    }

    public List<LigneBus> getLignesActives() {
        return ligneBusRepository.findByActif(true);
    }

    public Optional<LigneBus> getLigneById(String idLigne) {
        return ligneBusRepository.findById(idLigne);
    }

    public Optional<LigneBus> getLigneByNumero(String numeroLigne) {
        return ligneBusRepository.findByNumeroLigne(numeroLigne);
    }

    public LigneBus createLigne(LigneBus ligne) {
        return ligneBusRepository.save(ligne);
    }

    public LigneBus updateLigne(String idLigne, LigneBus ligneDetails) {
        return ligneBusRepository.findById(idLigne)
                .map(ligne -> {
                    ligne.setNumeroLigne(ligneDetails.getNumeroLigne());
                    ligne.setNomLigne(ligneDetails.getNomLigne());
                    ligne.setDescription(ligneDetails.getDescription());
                    ligne.setCouleur(ligneDetails.getCouleur());
                    ligne.setActif(ligneDetails.isActif());
                    return ligneBusRepository.save(ligne);
                })
                .orElseThrow(() -> new RuntimeException("Ligne non trouvée avec id: " + idLigne));
    }

    public void deleteLigne(String idLigne) {
        ligneBusRepository.deleteById(idLigne);
    }
}