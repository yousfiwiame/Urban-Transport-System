package com.transport.urbain.service;

import com.transport.urbain.dto.CreateRoutePricingRequest;
import com.transport.urbain.dto.RoutePricingResponse;
import com.transport.urbain.model.RoutePricing;
import com.transport.urbain.repository.RoutePricingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Service pour gérer les prix des routes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoutePricingService {

    private final RoutePricingRepository routePricingRepository;

    /**
     * Créer ou mettre à jour le pricing d'une route
     */
    public RoutePricingResponse createOrUpdatePricing(CreateRoutePricingRequest request) {
        log.info("Création/Mise à jour du pricing pour la route ID: {}", request.getRouteId());

        // Désactiver les anciens pricings pour cette route
        routePricingRepository.findFirstByRouteIdAndActiveTrue(request.getRouteId())
                .ifPresent(existingPricing -> {
                    existingPricing.setActive(false);
                    routePricingRepository.save(existingPricing);
                });

        // Créer le nouveau pricing
        RoutePricing pricing = RoutePricing.builder()
                .routeId(request.getRouteId())
                .basePrice(request.getBasePrice())
                .peakHourPrice(request.getPeakHourPrice())
                .weekendPrice(request.getWeekendPrice())
                .currency("MAD")
                .active(true)
                .description(request.getDescription())
                .effectiveFrom(LocalDateTime.now())
                .build();

        pricing = routePricingRepository.save(pricing);
        log.info("Pricing créé avec succès - ID: {}", pricing.getId());

        return mapToResponse(pricing);
    }

    /**
     * Récupérer le prix actif pour une route
     */
    @Transactional(readOnly = true)
    public RoutePricingResponse getPricingByRouteId(Long routeId) {
        log.info("Récupération du pricing pour la route ID: {}", routeId);

        RoutePricing pricing = routePricingRepository
                .findActiveByRouteId(routeId, LocalDateTime.now())
                .orElseGet(() -> {
                    // Pricing par défaut si aucun n'est trouvé
                    log.warn("Aucun pricing trouvé pour la route ID: {}, utilisation du prix par défaut", routeId);
                    return RoutePricing.builder()
                            .routeId(routeId)
                            .basePrice(new BigDecimal("10.00")) // Prix par défaut
                            .currency("MAD")
                            .active(true)
                            .description("Prix par défaut")
                            .build();
                });

        return mapToResponse(pricing);
    }

    /**
     * Calculer le prix en fonction de l'heure et du jour
     */
    @Transactional(readOnly = true)
    public BigDecimal calculatePrice(Long routeId, LocalDateTime travelDateTime) {
        log.info("Calcul du prix pour la route ID: {} à {}", routeId, travelDateTime);

        RoutePricing pricing = routePricingRepository
                .findActiveByRouteId(routeId, LocalDateTime.now())
                .orElse(RoutePricing.builder()
                        .basePrice(new BigDecimal("10.00"))
                        .build());

        // Vérifier si c'est le weekend
        DayOfWeek dayOfWeek = travelDateTime.getDayOfWeek();
        if ((dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) 
            && pricing.getWeekendPrice() != null) {
            log.info("Prix weekend appliqué: {}", pricing.getWeekendPrice());
            return pricing.getWeekendPrice();
        }

        // Vérifier si c'est une heure de pointe (7h-9h ou 17h-19h)
        LocalTime time = travelDateTime.toLocalTime();
        boolean isPeakHour = (time.isAfter(LocalTime.of(7, 0)) && time.isBefore(LocalTime.of(9, 0))) ||
                            (time.isAfter(LocalTime.of(17, 0)) && time.isBefore(LocalTime.of(19, 0)));

        if (isPeakHour && pricing.getPeakHourPrice() != null) {
            log.info("Prix heure de pointe appliqué: {}", pricing.getPeakHourPrice());
            return pricing.getPeakHourPrice();
        }

        // Prix de base
        log.info("Prix de base appliqué: {}", pricing.getBasePrice());
        return pricing.getBasePrice();
    }

    /**
     * Supprimer un pricing
     */
    public void deletePricing(Long id) {
        log.info("Suppression du pricing ID: {}", id);
        routePricingRepository.deleteById(id);
    }

    /**
     * Mapper l'entité vers le DTO de réponse
     */
    private RoutePricingResponse mapToResponse(RoutePricing pricing) {
        return RoutePricingResponse.builder()
                .id(pricing.getId())
                .routeId(pricing.getRouteId())
                .basePrice(pricing.getBasePrice())
                .peakHourPrice(pricing.getPeakHourPrice())
                .weekendPrice(pricing.getWeekendPrice())
                .currency(pricing.getCurrency())
                .active(pricing.getActive())
                .description(pricing.getDescription())
                .build();
    }
}

