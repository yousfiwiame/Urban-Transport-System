package com.transport.ticket.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour la génération de QR codes.
 */
@Service
@Slf4j
public class QRCodeService {

    private static final int QR_CODE_SIZE = 300;

    /**
     * Génère un QR code contenant les informations du billet.
     *
     * @param ticketData Données du billet à encoder dans le QR code
     * @return Image du QR code en bytes
     * @throws WriterException Si erreur lors de la génération
     * @throws IOException     Si erreur lors de l'écriture
     */
    public byte[] generateQRCode(String ticketData) throws WriterException, IOException {
        log.debug("Génération du QR code pour: {}", ticketData);

        // Configuration du QR code
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                ticketData,
                BarcodeFormat.QR_CODE,
                QR_CODE_SIZE,
                QR_CODE_SIZE,
                hints
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        log.debug("QR code généré avec succès");
        return outputStream.toByteArray();
    }

    /**
     * Génère les données du billet à encoder dans le QR code.
     *
     * @param ticketId          ID du billet
     * @param userId            ID de l'utilisateur
     * @param routeInfo         Informations sur la route
     * @param purchaseDate      Date d'achat
     * @return Chaîne de caractères formatée pour le QR code
     */
    public String generateTicketData(Long ticketId, Long userId, String routeInfo, String purchaseDate) {
        return String.format("TICKET:%d|USER:%d|ROUTE:%s|DATE:%s",
                ticketId, userId, routeInfo, purchaseDate);
    }
}

