package com.transport.urbain.controller;

import com.transport.urbain.dto.CreateRoutePricingRequest;
import com.transport.urbain.dto.RoutePricingResponse;
import com.transport.urbain.service.RoutePricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Controller REST pour gérer les prix des routes
 */
@RestController
@RequestMapping("/api/routes/pricing")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RoutePricingController {

    private final RoutePricingService routePricingService;

    /**
     * Créer ou mettre à jour le pricing d'une route (ADMIN uniquement)
     * POST /api/routes/pricing
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoutePricingResponse> createOrUpdatePricing(
            @Valid @RequestBody CreateRoutePricingRequest request) {
        log.info("Requête de création/mise à jour de pricing pour la route ID: {}", request.getRouteId());
        RoutePricingResponse response = routePricingService.createOrUpdatePricing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupérer le pricing d'une route
     * GET /api/routes/pricing/route/{routeId}
     */
    @GetMapping("/route/{routeId}")
    public ResponseEntity<RoutePricingResponse> getPricingByRouteId(@PathVariable Long routeId) {
        log.info("Requête de récupération du pricing pour la route ID: {}", routeId);
        RoutePricingResponse response = routePricingService.getPricingByRouteId(routeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Calculer le prix pour une route à une date/heure donnée
     * GET /api/routes/pricing/route/{routeId}/calculate
     */
    @GetMapping("/route/{routeId}/calculate")
    public ResponseEntity<BigDecimal> calculatePrice(
            @PathVariable Long routeId,
            @RequestParam(required = false) String dateTime) {
        log.info("Requête de calcul de prix pour la route ID: {}", routeId);
        
        LocalDateTime travelDateTime = dateTime != null 
            ? LocalDateTime.parse(dateTime) 
            : LocalDateTime.now();
        
        BigDecimal price = routePricingService.calculatePrice(routeId, travelDateTime);
        return ResponseEntity.ok(price);
    }

    /**
     * Supprimer un pricing (ADMIN uniquement)
     * DELETE /api/routes/pricing/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        log.info("Requête de suppression du pricing ID: {}", id);
        routePricingService.deletePricing(id);
        return ResponseEntity.noContent().build();
    }
}

