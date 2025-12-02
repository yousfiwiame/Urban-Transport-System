package com.transport.ticket.service;

import com.transport.ticket.dto.mapper.TicketMapper;
import com.transport.ticket.dto.mapper.TransactionMapper;  // ← AJOUTER
import com.transport.ticket.dto.request.PurchaseTicketRequest;
import com.transport.ticket.dto.response.PurchaseTicketResponse;
import com.transport.ticket.dto.response.TicketResponse;
import com.transport.ticket.dto.response.TransactionResponse;
import com.transport.ticket.exception.TicketNotFoundException;
import com.transport.ticket.model.*;
import com.transport.ticket.repository.TicketRepository;
import com.transport.ticket.repository.TransactionRepository;
import com.transport.ticket.service.QRCodeService;
import com.transport.ticket.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du TicketService")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private QRCodeService qrCodeService;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private com.transport.ticket.util.QRCodeGenerator qrCodeGenerator;

    @Mock
    private com.transport.ticket.service.TicketPDFService ticketPDFService;

    @Mock
    private com.transport.urbain.event.producer.TicketEventProducer ticketEventProducer;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private PurchaseTicketRequest purchaseRequest;
    private Ticket ticket;
    private Transaction transaction;
    private TicketResponse ticketResponse;
    private TransactionResponse transactionResponse;
    private PurchaseTicketResponse purchaseResponse;

    @BeforeEach
    void setUp() {
        purchaseRequest = PurchaseTicketRequest.builder()
                .idPassager(1L)
                .idTrajet(5L)
                .prix(new BigDecimal("15.50"))
                .methodePaiement("CREDIT_CARD")
                .build();

        ticket = Ticket.builder()
                .idTicket(1L)
                .idPassager(1L)
                .ticketNumber("TKT-12345")
                .statut(TicketStatus.ACTIVE)
                .prix(new BigDecimal("15.50"))
                .dateAchat(LocalDateTime.now())
                .build();

        transaction = Transaction.builder()
                .idTransaction(1L)
                .ticketId(1L)
                .montant(new BigDecimal("15.50"))
                .statut(PaymentStatus.COMPLETED)
                .methodePaiement(PaymentMethod.CREDIT_CARD)
                .build();

        ticketResponse = TicketResponse.builder()
                .idTicket(1L)
                .ticketNumber("TKT-12345")
                .idPassager(1L)
                .statut("ACTIVE")
                .prix(new BigDecimal("15.50"))
                .build();

        transactionResponse = TransactionResponse.builder()
                .idTransaction(1L)
                .montant(new BigDecimal("15.50"))
                .statut("SUCCESS")
                .build();

        purchaseResponse = PurchaseTicketResponse.builder()
                .ticket(ticketResponse)
                .transaction(transactionResponse)
                .success(true)
                .build();
    }

    @Test
    @DisplayName("Devrait acheter un ticket avec succès")
    void shouldPurchaseTicketSuccessfully() throws Exception {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(qrCodeService.generateTicketData(anyLong(), anyLong(), anyString(), anyString()))
            .thenReturn("TICKET:1|USER:1|ROUTE:Route-5|DATE:25/11/2024");
        when(qrCodeService.generateQRCode(anyString())).thenReturn(new byte[]{1, 2, 3, 4});
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(ticketResponse);
        when(transactionMapper.toResponse(any(Transaction.class))).thenReturn(transactionResponse);

        PurchaseTicketResponse response = ticketService.purchaseTicket(purchaseRequest);

        assertThat(response).isNotNull();
        assertThat(response.getSuccess()).isTrue();

        verify(ticketRepository, times(2)).save(any(Ticket.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(qrCodeService, times(1)).generateTicketData(anyLong(), anyLong(), anyString(), anyString());
        verify(qrCodeService, times(1)).generateQRCode(anyString());
    }

    @Test
    @DisplayName("Devrait récupérer un ticket par ID")
    void shouldGetTicketById() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketMapper.toResponse(ticket)).thenReturn(ticketResponse);

        TicketResponse response = ticketService.getTicketById(1L);

        assertThat(response).isNotNull();
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketMapper, times(1)).toResponse(ticket);
    }

    @Test
    @DisplayName("Devrait lever une exception si ticket non trouvé")
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getTicketById(999L))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("Ticket non trouvé avec l'ID: 999");
    }

    @Test
    @DisplayName("Devrait récupérer tous les tickets d'un passager")
    void shouldGetAllPassengerTickets() {
        List<Ticket> tickets = Arrays.asList(ticket, ticket);
        when(ticketRepository.findByIdPassager(1L)).thenReturn(tickets);
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(ticketResponse);

        List<TicketResponse> responses = ticketService.getPassengerTickets(1L);

        assertThat(responses).hasSize(2);
        verify(ticketRepository, times(1)).findByIdPassager(1L);
    }

    @Test
    @DisplayName("Devrait annuler un ticket actif")
    void shouldCancelActiveTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(ticketMapper.toResponse(any(Ticket.class))).thenReturn(ticketResponse);

        TicketResponse response = ticketService.cancelTicket(1L, "Changement de plans");

        assertThat(response).isNotNull();
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Devrait compter les tickets actifs")
    void shouldCountActiveTickets() {
        when(ticketRepository.countByIdPassagerAndStatut(1L, TicketStatus.ACTIVE)).thenReturn(3L);

        long count = ticketService.countActiveTickets(1L);

        assertThat(count).isEqualTo(3);
        verify(ticketRepository, times(1)).countByIdPassagerAndStatut(1L, TicketStatus.ACTIVE);
    }
}