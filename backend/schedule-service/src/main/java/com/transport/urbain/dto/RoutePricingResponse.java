package com.transport.urbain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de réponse pour le pricing des routes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutePricingResponse {
    private Long id;
    private Long routeId;
    private BigDecimal basePrice;
    private BigDecimal peakHourPrice;
    private BigDecimal weekendPrice;
    private String currency;
    private Boolean active;
    private String description;
}

