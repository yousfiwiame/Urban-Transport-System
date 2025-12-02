package com.transport.urbain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a ticket is validated/scanned.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketValidatedEvent {
    private Long ticketId;
    private Long userId;
    private String ticketNumber;
    private Long busId;
    private String scanLocation;
    private LocalDateTime validatedAt;
}
