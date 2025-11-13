package com.transport.ticket.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

/**
 * Générateur de QR codes pour les tickets
 * Dans une vraie application, utilisez une bibliothèque comme ZXing
 */
@Component
@Slf4j
public class QRCodeGenerator {

    /**
     * Génère un QR code simple (encodé en Base64)
     * Format: TICKET-{ticketNumber}-{uuid}
     */
    public String generateQRCode(String ticketNumber) {
        log.debug("Génération du QR code pour le ticket: {}", ticketNumber);

        try {
            // Créer une chaîne unique
            String qrData = String.format("TICKET-%s-%s",
                    ticketNumber,
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase()
            );

            // Encoder en Base64 (simulation de QR code)
            String qrCode = Base64.getEncoder().encodeToString(qrData.getBytes());

            log.debug("QR code généré avec succès");
            return qrCode;

        } catch (Exception e) {
            log.error("Erreur lors de la génération du QR code: {}", e.getMessage());
            return "ERROR_GENERATING_QR_CODE";
        }
    }

    /**
     * Décoder un QR code
     */
    public String decodeQRCode(String qrCode) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(qrCode);
            return new String(decodedBytes);
        } catch (Exception e) {
            log.error("Erreur lors du décodage du QR code: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valider un QR code
     */
    public boolean isValidQRCode(String qrCode) {
        try {
            String decoded = decodeQRCode(qrCode);
            return decoded != null && decoded.startsWith("TICKET-");
        } catch (Exception e) {
            return false;
        }
    }
}