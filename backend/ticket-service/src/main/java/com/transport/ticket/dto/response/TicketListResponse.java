package com.transport.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de réponse pour une liste de tickets (avec pagination)
 * Renvoyé par : GET /api/tickets/passager/{id}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketListResponse {

    private List<TicketResponse> tickets;

    private Long totalElements;

    private Integer totalPages;

    private Integer currentPage;

    private Integer pageSize;

    private Boolean hasNext;

    private Boolean hasPrevious;
}