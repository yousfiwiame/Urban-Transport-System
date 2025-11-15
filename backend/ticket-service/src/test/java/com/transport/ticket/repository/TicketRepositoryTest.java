package com.transport.ticket.repository;

import com.transport.ticket.model.Ticket;
import com.transport.ticket.model.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du TicketRepository")
class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = Ticket.builder()
                .idPassager(1L)
                .idTrajet(5L)
                .prix(new BigDecimal("15.50"))
                .statut(TicketStatus.ACTIVE)
                .ticketNumber("TKT-TEST-12345")
                .validFrom(LocalDateTime.now())
                .validUntil(LocalDateTime.now().plusHours(24))
                .build();
    }

    @Test
    @DisplayName("Devrait sauvegarder un ticket")
    void shouldSaveTicket() {
        Ticket savedTicket = ticketRepository.save(ticket);

        assertThat(savedTicket).isNotNull();
        assertThat(savedTicket.getIdTicket()).isNotNull();
        assertThat(savedTicket.getTicketNumber()).isEqualTo("TKT-TEST-12345");
        assertThat(savedTicket.getStatut()).isEqualTo(TicketStatus.ACTIVE);
        assertThat(savedTicket.getPrix()).isEqualByComparingTo(new BigDecimal("15.50"));
    }

    @Test
    @DisplayName("Devrait trouver un ticket par son numéro")
    void shouldFindTicketByNumber() {
        entityManager.persist(ticket);
        entityManager.flush();

        Optional<Ticket> found = ticketRepository.findByTicketNumber("TKT-TEST-12345");

        assertThat(found).isPresent();
        assertThat(found.get().getTicketNumber()).isEqualTo("TKT-TEST-12345");
        assertThat(found.get().getPrix()).isEqualByComparingTo(new BigDecimal("15.50"));
    }

    @Test
    @DisplayName("Devrait compter les tickets actifs d'un passager")
    void shouldCountActiveTickets() {
        Ticket activeTicket = Ticket.builder()
                .idPassager(1L)
                .idTrajet(5L)
                .prix(new BigDecimal("15.50"))
                .statut(TicketStatus.ACTIVE)
                .ticketNumber("TKT-ACTIVE")
                .build();

        Ticket usedTicket = Ticket.builder()
                .idPassager(1L)
                .idTrajet(6L)
                .prix(new BigDecimal("20.00"))
                .statut(TicketStatus.USED)
                .ticketNumber("TKT-USED")
                .build();

        entityManager.persist(activeTicket);
        entityManager.persist(usedTicket);
        entityManager.flush();

        long count = ticketRepository.countByIdPassagerAndStatut(1L, TicketStatus.ACTIVE);

        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("Ne devrait pas trouver de ticket avec un numéro inexistant")
    void shouldNotFindTicketWithInvalidNumber() {
        Optional<Ticket> found = ticketRepository.findByTicketNumber("INVALID");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Devrait trouver tous les tickets d'un passager")
    void shouldFindAllTicketsByPassenger() {
        Ticket ticket1 = createTicket(1L, "TKT-1", TicketStatus.ACTIVE);
        Ticket ticket2 = createTicket(1L, "TKT-2", TicketStatus.USED);
        Ticket ticket3 = createTicket(1L, "TKT-3", TicketStatus.EXPIRED);

        entityManager.persist(ticket1);
        entityManager.persist(ticket2);
        entityManager.persist(ticket3);
        entityManager.flush();

        List<Ticket> tickets = ticketRepository.findByIdPassager(1L);

        assertThat(tickets).hasSize(3);
        assertThat(tickets).extracting(Ticket::getIdPassager).containsOnly(1L);
        assertThat(tickets).extracting(Ticket::getTicketNumber)
                .containsExactlyInAnyOrder("TKT-1", "TKT-2", "TKT-3");
    }

    private Ticket createTicket(Long passagerId, String ticketNumber, TicketStatus status) {
        return Ticket.builder()
                .idPassager(passagerId)
                .ticketNumber(ticketNumber)
                .statut(status)
                .prix(new BigDecimal("15.50"))
                .idTrajet(5L)
                .validFrom(LocalDateTime.now())
                .validUntil(LocalDateTime.now().plusHours(24))
                .build();
    }
}