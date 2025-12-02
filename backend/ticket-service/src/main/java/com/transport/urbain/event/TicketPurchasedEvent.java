package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a ticket is purchased.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPurchasedEvent {
    private Long ticketId;
    private Long userId;
    private Long routeId;
    private BigDecimal price;
    private String ticketNumber;
    private String qrCode;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime purchasedAt;
}
