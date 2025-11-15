package com.transport.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import com.transport.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@DisplayName("Tests du TicketController")
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    private PurchaseTicketRequest purchaseRequest;
    private PurchaseTicketResponse purchaseResponse;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        purchaseRequest = PurchaseTicketRequest.builder()
                .idPassager(1L)
                .idTrajet(5L)
                .prix(new BigDecimal("15.50"))
                .methodePaiement("CREDIT_CARD")
                .build();

        ticketResponse = TicketResponse.builder()
                .idTicket(1L)
                .ticketNumber("TKT-12345")
                .idPassager(1L)
                .idTrajet(5L)
                .prix(new BigDecimal("15.50"))
                .statut("ACTIVE")
                .isValid(true)
                .isExpired(false)
                .build();

        purchaseResponse = PurchaseTicketResponse.builder()
                .ticket(ticketResponse)
                .success(true)
                .message("Ticket acheté avec succès !")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/tickets/purchase - Devrait acheter un ticket")
    void shouldPurchaseTicket() throws Exception {
        when(ticketService.purchaseTicket(any(PurchaseTicketRequest.class)))
                .thenReturn(purchaseResponse);

        mockMvc.perform(post("/api/tickets/purchase")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.ticket.ticketNumber").value("TKT-12345"))
                .andExpect(jsonPath("$.message").value("Ticket acheté avec succès !"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/{id} - Devrait récupérer un ticket")
    void shouldGetTicketById() throws Exception {
        when(ticketService.getTicketById(1L)).thenReturn(ticketResponse);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTicket").value(1))
                .andExpect(jsonPath("$.ticketNumber").value("TKT-12345"))
                .andExpect(jsonPath("$.statut").value("ACTIVE"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/passager/{id} - Devrait récupérer les tickets d'un passager")
    void shouldGetPassengerTickets() throws Exception {
        List<TicketResponse> tickets = Arrays.asList(ticketResponse, ticketResponse);
        when(ticketService.getPassengerTickets(1L)).thenReturn(tickets);

        mockMvc.perform(get("/api/tickets/passager/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ticketNumber").value("TKT-12345"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/tickets/{id} - Devrait annuler un ticket")
    void shouldCancelTicket() throws Exception {
        TicketResponse cancelledTicket = TicketResponse.builder()
                .idTicket(1L)
                .ticketNumber("TKT-12345")
                .statut("CANCELLED")
                .build();
        when(ticketService.cancelTicket(eq(1L), any(String.class))).thenReturn(cancelledTicket);

        mockMvc.perform(delete("/api/tickets/1")
                        .with(csrf())
                        .param("reason", "Changement de plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CANCELLED"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/tickets/health - Devrait retourner le status du service")
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/tickets/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Ticket Service"));
    }
}