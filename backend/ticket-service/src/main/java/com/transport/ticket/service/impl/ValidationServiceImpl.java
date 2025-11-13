package com.transport.ticket.service.impl;

import com.transport.ticket.dto.mapper.ValidationMapper;
import com.transport.ticket.dto.request.ValidateTicketRequest;
import com.transport.ticket.dto.response.ValidationResponse;
import com.transport.ticket.exception.InvalidTicketException;
import com.transport.ticket.exception.TicketNotFoundException;
import com.transport.ticket.model.Ticket;
import com.transport.ticket.model.TicketStatus;
import com.transport.ticket.model.TicketValidation;
import com.transport.ticket.repository.TicketRepository;
import com.transport.ticket.repository.TicketValidationRepository;
import com.transport.ticket.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ValidationServiceImpl implements ValidationService {

    private final TicketRepository ticketRepository;
    private final TicketValidationRepository validationRepository;
    private final ValidationMapper validationMapper;

    @Override
    public ValidationResponse validateTicket(ValidateTicketRequest request) {
        log.info("🔲 Validation du ticket: {}", request.getTicketNumber());

        try {
            // 1. Trouver le ticket
            Ticket ticket = ticketRepository.findByTicketNumber(request.getTicketNumber())
                    .orElseThrow(() -> {
                        log.error("❌ Ticket non trouvé: {}", request.getTicketNumber());
                        return new TicketNotFoundException("Ticket non trouvé: " + request.getTicketNumber());
                    });

            // 2. Vérifier le statut du ticket
            if (ticket.getStatut() != TicketStatus.ACTIVE) {
                String errorMsg = "Ticket non valide. Statut: " + ticket.getStatut();
                log.error("❌ {}", errorMsg);
                return validationMapper.toErrorResponse(request.getTicketNumber(), errorMsg);
            }

            // 3. Vérifier si le ticket est expiré
            if (ticket.getValidUntil().isBefore(LocalDateTime.now())) {
                log.error("❌ Ticket expiré");

                // Mettre à jour le statut
                ticket.setStatut(TicketStatus.EXPIRED);
                ticketRepository.save(ticket);

                return validationMapper.toErrorResponse(
                        request.getTicketNumber(),
                        "Ticket expiré le " + ticket.getValidUntil()
                );
            }

            // 4. Vérifier si le ticket a déjà été validé (pour les tickets à usage unique)
            boolean alreadyValidated = validationRepository.existsByTicketId(ticket.getIdTicket());
            if (alreadyValidated) {
                log.warn("⚠️ Ticket déjà validé précédemment");
                // Pour un pass journalier, on pourrait autoriser plusieurs validations
                // Pour l'instant, on bloque
                return validationMapper.toErrorResponse(
                        request.getTicketNumber(),
                        "Ticket déjà utilisé"
                );
            }

            // 5. Créer la validation
            TicketValidation validation = TicketValidation.builder()
                    .ticketId(ticket.getIdTicket())
                    .validationLocation(request.getValidationLocation())
                    .validatorId(request.getValidatorId())
                    .build();

            // dateValidation, heureValidation et validationTimestamp sont générés par @PrePersist
            validation = validationRepository.save(validation);

            // 6. Mettre à jour le statut du ticket
            ticket.setStatut(TicketStatus.USED);
            ticketRepository.save(ticket);

            log.info("✅ Ticket {} validé avec succès à {} par le validateur {}",
                    request.getTicketNumber(),
                    request.getValidationLocation(),
                    request.getValidatorId());

            return validationMapper.toResponse(validation, ticket.getTicketNumber());

        } catch (TicketNotFoundException | InvalidTicketException e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la validation du ticket: {}", e.getMessage(), e);
            return validationMapper.toErrorResponse(
                    request.getTicketNumber(),
                    "Erreur technique lors de la validation"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ValidationResponse> getTicketValidationHistory(Long ticketId) {
        log.info("🔍 Recherche de l'historique de validation du ticket ID: {}", ticketId);

        // Vérifier que le ticket existe
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket non trouvé avec l'ID: " + ticketId));

        List<TicketValidation> validations = validationRepository.findByTicketId(ticketId);

        log.info("✅ {} validation(s) trouvée(s)", validations.size());

        return validations.stream()
                .map(v -> validationMapper.toResponse(v, ticket.getTicketNumber()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTicketAlreadyValidated(Long ticketId) {
        log.info("🔍 Vérification si le ticket ID: {} a déjà été validé", ticketId);

        boolean validated = validationRepository.existsByTicketId(ticketId);

        log.info("✅ Ticket {} été validé", validated ? "a" : "n'a pas");

        return validated;
    }
}