package com.transport.subscription.util;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Utilitaire pour valider les informations de carte bancaire.
 */
public class CardValidationUtil {

    /**
     * Vérifie si une carte est expirée en fonction de son mois et année d'expiration.
     * 
     * @param expMonth Mois d'expiration (1-12)
     * @param expYear Année d'expiration (ex: 2025)
     * @return true si la carte est expirée, false sinon
     */
    public static boolean isCardExpired(Integer expMonth, Integer expYear) {
        if (expMonth == null || expYear == null) {
            return true; // Considérer comme expirée si les données sont manquantes
        }

        YearMonth cardExpiration = YearMonth.of(expYear, expMonth);
        YearMonth currentMonth = YearMonth.now();

        // La carte est expirée si le mois d'expiration est avant le mois actuel
        return cardExpiration.isBefore(currentMonth);
    }

    /**
     * Vérifie si une carte expire dans les X prochains mois.
     * Utile pour avertir l'utilisateur qu'il doit mettre à jour sa carte.
     * 
     * @param expMonth Mois d'expiration (1-12)
     * @param expYear Année d'expiration
     * @param monthsAhead Nombre de mois à vérifier (ex: 3 pour vérifier si expire dans 3 mois)
     * @return true si la carte expire dans les X prochains mois
     */
    public static boolean isCardExpiringSoon(Integer expMonth, Integer expYear, int monthsAhead) {
        if (expMonth == null || expYear == null) {
            return false;
        }

        YearMonth cardExpiration = YearMonth.of(expYear, expMonth);
        YearMonth warningDate = YearMonth.now().plusMonths(monthsAhead);

        return !cardExpiration.isAfter(warningDate) && !isCardExpired(expMonth, expYear);
    }

    /**
     * Valide que la date d'expiration de la carte est valide (mois entre 1-12, année future ou actuelle).
     * 
     * @param expMonth Mois d'expiration
     * @param expYear Année d'expiration
     * @return true si la date est valide
     */
    public static boolean isValidExpirationDate(Integer expMonth, Integer expYear) {
        if (expMonth == null || expYear == null) {
            return false;
        }

        // Vérifier que le mois est valide
        if (expMonth < 1 || expMonth > 12) {
            return false;
        }

        // Vérifier que l'année n'est pas trop ancienne (au moins année actuelle - 1)
        int currentYear = LocalDate.now().getYear();
        if (expYear < currentYear - 1) {
            return false;
        }

        return true;
    }
}

