package com.transport.urbain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour créer un pricing de route
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoutePricingRequest {
    
    @NotNull(message = "L'ID de la route est obligatoire")
    private Long routeId;

    @NotNull(message = "Le prix de base est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private BigDecimal basePrice;

    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private BigDecimal peakHourPrice;

    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private BigDecimal weekendPrice;

    private String description;
}

