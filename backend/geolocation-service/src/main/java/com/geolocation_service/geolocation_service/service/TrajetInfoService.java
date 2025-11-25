package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.dto.*;
import com.geolocation_service.geolocation_service.model.*;
import com.geolocation_service.geolocation_service.repository.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrajetInfoService {

    private final BusRepository busRepository;
    private final PositionBusRepository positionBusRepository;

    public TrajetInfoService(BusRepository busRepository,
                             PositionBusRepository positionBusRepository) {
        this.busRepository = busRepository;
        this.positionBusRepository = positionBusRepository;
    }

    public TrajetInfoDTO getTrajetInfo(String busId) {
        // Récupérer le bus
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus non trouvé avec id: " + busId));

        // Récupérer la dernière position (nouveau système avec Long busId)
        // Convertir String busId en Long pour le nouveau système
        Long busIdLong;
        try {
            busIdLong = Long.parseLong(busId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("ID de bus invalide: " + busId);
        }
        List<PositionBus> positions = positionBusRepository.findByBusIdOrderByTimestampDesc(busIdLong);

        if (positions.isEmpty()) {
            throw new RuntimeException("Aucune position trouvée pour le bus: " + busId);
        }

        PositionBus lastPosition = positions.get(0);

        // Créer le DTO
        TrajetInfoDTO dto = new TrajetInfoDTO();
        dto.setIdBus(bus.getIdBus());
        dto.setImmatriculation(bus.getImmatriculation());

        // Informations ligne et direction
        if (bus.getLigneActuelle() != null) {
            LigneBusDTO ligneDTO = new LigneBusDTO();
            ligneDTO.setIdLigne(bus.getLigneActuelle().getIdLigne());
            ligneDTO.setNumeroLigne(bus.getLigneActuelle().getNumeroLigne());
            ligneDTO.setNomLigne(bus.getLigneActuelle().getNomLigne());
            ligneDTO.setCouleur(bus.getLigneActuelle().getCouleur());
            dto.setLigne(ligneDTO);
        }

        if (bus.getDirectionActuelle() != null) {
            DirectionDTO directionDTO = new DirectionDTO();
            directionDTO.setIdDirection(bus.getDirectionActuelle().getIdDirection());
            directionDTO.setNomDirection(bus.getDirectionActuelle().getNomDirection());
            directionDTO.setPointDepart(bus.getDirectionActuelle().getPointDepart());
            directionDTO.setPointArrivee(bus.getDirectionActuelle().getPointArrivee());
            dto.setDirection(directionDTO);
        }

        // Position actuelle
        dto.setLatitudeActuelle(lastPosition.getLatitude());
        dto.setLongitudeActuelle(lastPosition.getLongitude());
        dto.setVitesseActuelle(lastPosition.getVitesse());
        dto.setDerniereMiseAJour(lastPosition.getTimestamp());

        // Calculer les statistiques du trajet en cours
        calculerStatistiquesTrajet(dto, busId, lastPosition.getTimestamp());

        return dto;
    }

    private void calculerStatistiquesTrajet(TrajetInfoDTO dto, String busId, LocalDateTime now) {
        // Récupérer les positions des 2 dernières heures (nouveau système avec Long busId)
        // Convertir String busId en Long pour le nouveau système
        Long busIdLong;
        try {
            busIdLong = Long.parseLong(busId);
        } catch (NumberFormatException e) {
            // Si conversion échoue, utiliser 0L (ne trouvera rien mais ne plantera pas)
            busIdLong = 0L;
        }
        LocalDateTime debutPeriode = now.minusHours(2);
        List<PositionBus> positions = positionBusRepository
                .findByBusIdAndTimestampBetweenOrderByTimestampAsc(busIdLong, debutPeriode, now);

        if (positions.isEmpty()) {
            dto.setHeureDepart(now);
            dto.setDistanceParcourue(0.0);
            dto.setDureeTrajetMinutes(0);
            dto.setNombreArretsEffectues(0);
            return;
        }

        // Heure de départ = timestamp de la première position
        dto.setHeureDepart(positions.get(0).getTimestamp());

        // Calculer la distance parcourue (formule de Haversine)
        double distanceTotale = 0.0;
        for (int i = 1; i < positions.size(); i++) {
            PositionBus p1 = positions.get(i - 1);
            PositionBus p2 = positions.get(i);
            distanceTotale += calculerDistance(
                    p1.getLatitude(), p1.getLongitude(),
                    p2.getLatitude(), p2.getLongitude()
            );
        }
        dto.setDistanceParcourue(Math.round(distanceTotale * 100.0) / 100.0);

        // Durée du trajet
        Duration duree = Duration.between(positions.get(0).getTimestamp(), now);
        dto.setDureeTrajetMinutes((int) duree.toMinutes());

        // Compter les arrêts (vitesse < 5 km/h pendant plus de 30 secondes)
        int nombreArrets = compterArrets(positions);
        dto.setNombreArretsEffectues(nombreArrets);

        // Informations prochaine étape (exemple simplifié)
        if (dto.getDirection() != null) {
            dto.setProchainArret(dto.getDirection().getPointArrivee());
            dto.setDistanceProchainArret(5.2); // À calculer avec vraies coordonnées
            dto.setTempsEstimeProchainArret(15); // À calculer avec vitesse moyenne
        }
    }

    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        // Formule de Haversine pour calculer la distance entre deux points GPS
        final int R = 6371; // Rayon de la Terre en km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance en km
    }

    private int compterArrets(List<PositionBus> positions) {
        int arrets = 0;
        boolean enArret = false;

        for (PositionBus position : positions) {
            if (position.getVitesse() < 5 && !enArret) {
                arrets++;
                enArret = true;
            } else if (position.getVitesse() >= 5) {
                enArret = false;
            }
        }

        return arrets;
    }
}