package com.transport.ticket.repository;

import com.transport.ticket.model.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketValidationRepository extends JpaRepository<TicketValidation, Long> {

    // ==================== RECHERCHES DE BASE ====================

    /**
     * Trouver toutes les validations d'un ticket
     * Utilisé pour : Historique d'utilisation d'un ticket (pass journalier)
     */
    List<TicketValidation> findByTicketId(Long ticketId);

    /**
     * Trouver la dernière validation d'un ticket
     * Utilisé pour : Vérifier quand le ticket a été utilisé pour la dernière fois
     */
    Optional<TicketValidation> findFirstByTicketIdOrderByValidationTimestampDesc(Long ticketId);

    /**
     * Vérifier si un ticket a déjà été validé
     * Utilisé pour : Empêcher la double validation de tickets à usage unique
     */
    boolean existsByTicketId(Long ticketId);

    /**
     * Compter le nombre de fois qu'un ticket a été validé
     * Utilisé pour : Pass journalier (limite de validations)
     */
    long countByTicketId(Long ticketId);

    // ==================== RECHERCHES PAR DATE ====================

    /**
     * Trouver les validations pour une date spécifique
     * Utilisé pour : Rapports journaliers
     */
    List<TicketValidation> findByDateValidation(LocalDate dateValidation);

    /**
     * Trouver les validations entre deux dates
     * Utilisé pour : Statistiques sur une période
     */
    @Query("SELECT v FROM TicketValidation v WHERE v.validationTimestamp BETWEEN :startDate AND :endDate")
    List<TicketValidation> findValidationsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Trouver les validations récentes (dernières 24h)
     * Utilisé pour : Monitoring en temps réel
     */
    @Query("SELECT v FROM TicketValidation v WHERE v.validationTimestamp > :since")
    List<TicketValidation> findRecentValidations(@Param("since") LocalDateTime since);

    // ==================== RECHERCHES PAR LIEU ====================

    /**
     * Trouver les validations à un lieu spécifique
     * Utilisé pour : Statistiques par arrêt/station
     */
    List<TicketValidation> findByValidationLocation(String validationLocation);

    /**
     * Compter les validations par lieu
     * Utilisé pour : Identifier les arrêts les plus fréquentés
     */
    @Query("SELECT v.validationLocation, COUNT(v) FROM TicketValidation v GROUP BY v.validationLocation")
    List<Object[]> countValidationsByLocation();

    // ==================== RECHERCHES PAR VALIDATEUR ====================

    /**
     * Trouver les validations effectuées par un contrôleur/validateur
     * Utilisé pour : Suivi du travail des contrôleurs
     */
    List<TicketValidation> findByValidatorId(Long validatorId);

    /**
     * Compter les validations par validateur
     * Utilisé pour : Performance des contrôleurs
     */
    @Query("SELECT v.validatorId, COUNT(v) FROM TicketValidation v GROUP BY v.validatorId")
    List<Object[]> countValidationsByValidator();

    // ==================== STATISTIQUES ====================

    /**
     * Compter les validations par jour
     * Utilisé pour : Graphique d'utilisation quotidienne
     */
    @Query("SELECT v.dateValidation, COUNT(v) FROM TicketValidation v GROUP BY v.dateValidation ORDER BY v.dateValidation DESC")
    List<Object[]> countValidationsByDay();

    /**
     * Compter le nombre total de validations aujourd'hui
     * Utilisé pour : Dashboard en temps réel
     */
    @Query("SELECT COUNT(v) FROM TicketValidation v WHERE v.dateValidation = CURRENT_DATE")
    long countTodayValidations();

    /**
     * Trouver les heures de pointe (plus de validations)
     * Utilisé pour : Optimiser les horaires de bus
     */
    @Query("SELECT HOUR(v.validationTimestamp), COUNT(v) FROM TicketValidation v " +
            "GROUP BY HOUR(v.validationTimestamp) ORDER BY COUNT(v) DESC")
    List<Object[]> findPeakHours();

    /**
     * Vérifier si un ticket a été validé aujourd'hui
     * Utilisé pour : Pass journaliers
     */
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM TicketValidation v " +
            "WHERE v.ticketId = :ticketId AND v.dateValidation = CURRENT_DATE")
    boolean isTicketValidatedToday(@Param("ticketId") Long ticketId);

    /**
     * Trouver les 10 dernières validations
     * Utilisé pour : Dashboard admin
     */
    List<TicketValidation> findTop10ByOrderByValidationTimestampDesc();
}