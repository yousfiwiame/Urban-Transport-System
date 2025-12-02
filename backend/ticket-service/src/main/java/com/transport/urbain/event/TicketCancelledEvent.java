package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a ticket is cancelled.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCancelledEvent {
    private Long ticketId;
    private Long userId;
    private Long routeId;
    private BigDecimal refundAmount;
    private String ticketNumber;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}
