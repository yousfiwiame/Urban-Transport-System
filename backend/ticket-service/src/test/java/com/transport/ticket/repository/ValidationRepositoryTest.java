package com.transport.ticket.repository;

import com.transport.ticket.model.TicketValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Tests du ValidationRepository")
class ValidationRepositoryTest {

    @Autowired
    private TicketValidationRepository validationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Devrait sauvegarder une validation")
    void shouldSaveValidation() {
        TicketValidation validation = TicketValidation.builder()
                .ticketId(1L)
                .dateValidation(LocalDate.now())
                .heureValidation(LocalTime.now())
                .validationTimestamp(LocalDateTime.now())
                .validationLocation("Station Hassan")
                .validatorId(42L)
                .build();

        TicketValidation saved = validationRepository.save(validation);

        assertThat(saved).isNotNull();
        assertThat(saved.getIdValidation()).isNotNull();
        assertThat(saved.getValidationLocation()).isEqualTo("Station Hassan");
        assertThat(saved.getValidatorId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("Devrait trouver l'historique de validation d'un ticket")
    void shouldFindValidationHistory() {
        TicketValidation v1 = createValidation(1L, "Station Hassan");
        TicketValidation v2 = createValidation(1L, "Station Agdal");

        entityManager.persist(v1);
        entityManager.persist(v2);
        entityManager.flush();

        List<TicketValidation> history = validationRepository.findByTicketId(1L);

        assertThat(history).hasSize(2);
        assertThat(history).extracting(TicketValidation::getValidationLocation)
                .containsExactlyInAnyOrder("Station Hassan", "Station Agdal");
    }

    @Test
    @DisplayName("Devrait vérifier si un ticket a été validé")
    void shouldCheckIfTicketValidated() {
        TicketValidation validation = createValidation(1L, "Station Hassan");
        entityManager.persist(validation);
        entityManager.flush();

        boolean exists = validationRepository.existsByTicketId(1L);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Devrait retourner false pour un ticket non validé")
    void shouldReturnFalseForNonValidatedTicket() {
        boolean exists = validationRepository.existsByTicketId(999L);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Devrait trouver les validations par location")
    void shouldFindValidationsByLocation() {
        TicketValidation v1 = createValidation(1L, "Station Hassan");
        TicketValidation v2 = createValidation(2L, "Station Hassan");
        TicketValidation v3 = createValidation(3L, "Station Agdal");

        entityManager.persist(v1);
        entityManager.persist(v2);
        entityManager.persist(v3);
        entityManager.flush();

        List<TicketValidation> validations = validationRepository
                .findByValidationLocation("Station Hassan");

        assertThat(validations).hasSize(2);
        assertThat(validations).extracting(TicketValidation::getValidationLocation)
                .containsOnly("Station Hassan");
    }

    private TicketValidation createValidation(Long ticketId, String location) {
        return TicketValidation.builder()
                .ticketId(ticketId)
                .dateValidation(LocalDate.now())
                .heureValidation(LocalTime.now())
                .validationTimestamp(LocalDateTime.now())
                .validationLocation(location)
                .validatorId(42L)
                .build();
    }
}