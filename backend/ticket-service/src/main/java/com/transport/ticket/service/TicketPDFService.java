package com.transport.ticket.service;

import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.transport.ticket.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Service pour la génération de billets PDF.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TicketPDFService {

    private final QRCodeService qrCodeService;

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(220, 53, 69); // Rouge
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(52, 58, 64); // Gris foncé
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Génère un PDF de billet pour un ticket donné.
     *
     * @param ticket      Le ticket
     * @param userName    Nom de l'utilisateur
     * @param routeInfo   Informations sur la route
     * @return Bytes du PDF généré
     * @throws IOException     Si erreur d'écriture
     * @throws WriterException Si erreur de génération du QR code
     */
    public byte[] generateTicketPDF(Ticket ticket, String userName, String routeInfo) 
            throws IOException, WriterException {
        
        log.info("Génération du PDF pour le ticket ID: {}", ticket.getIdTicket());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // Header
        addHeader(document);

        // Titre principal
        addTitle(document);

        // Informations du passager et du voyage
        addPassengerInfo(document, ticket, userName);

        // Section route avec flèche
        addRouteSection(document, routeInfo);

        // Prix
        addPriceSection(document, ticket);

        // QR Code
        addQRCodeSection(document, ticket);

        // Footer
        addFooter(document);

        document.close();
        log.info("PDF généré avec succès pour le ticket ID: {}", ticket.getIdTicket());
        
        return baos.toByteArray();
    }

    private void addHeader(Document document) {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        // Logo gauche
        Paragraph leftLogo = new Paragraph("Transport Urbain")
                .setFontSize(18)
                .setBold()
                .setFontColor(PRIMARY_COLOR);
        Cell leftCell = new Cell().add(leftLogo).setBorder(Border.NO_BORDER);

        // Logo droit
        Paragraph rightLogo = new Paragraph("URBAN BUS")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.RIGHT);
        Cell rightCell = new Cell().add(rightLogo).setBorder(Border.NO_BORDER);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addTitle(Document document) {
        Paragraph title = new Paragraph("CONFIRMATION E-BILLET / E-TICKET CONFIRMATION")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(PRIMARY_COLOR);
        
        document.add(title);
        
        SolidLine line = new SolidLine();
        line.setColor(PRIMARY_COLOR);
        document.add(new LineSeparator(line));
        document.add(new Paragraph("\n"));
    }

    private void addPassengerInfo(Document document, Ticket ticket, String userName) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                .useAllAvailableWidth();

        // Nom
        infoTable.addCell(createLabelCell("Nom / Last Name:"));
        infoTable.addCell(createValueCell(userName.toUpperCase()));

        // Numéro de billet
        infoTable.addCell(createLabelCell("N° Billet / Ticket No:"));
        infoTable.addCell(createValueCell(ticket.getIdTicket().toString()));

        // ID Client
        infoTable.addCell(createLabelCell("ID Client / Customer ID:"));
        infoTable.addCell(createValueCell(ticket.getIdPassager().toString()));

        // Transaction
        infoTable.addCell(createLabelCell("Transaction N°:"));
        infoTable.addCell(createValueCell("TRX-" + ticket.getIdTicket()));

        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    private void addRouteSection(Document document, String routeInfo) {
        // Parse route info (format: "Origin - Destination")
        String[] parts = routeInfo.split(" - ");
        String origin = parts.length > 0 ? parts[0] : "N/A";
        String destination = parts.length > 1 ? parts[1] : "N/A";

        Table routeTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Origine
        Cell originCell = new Cell()
                .add(new Paragraph(origin)
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(SECONDARY_COLOR))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER);

        // Flèche
        Cell arrowCell = new Cell()
                .add(new Paragraph("⇄")
                        .setFontSize(24)
                        .setFontColor(PRIMARY_COLOR))
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(Border.NO_BORDER);

        // Destination
        Cell destinationCell = new Cell()
                .add(new Paragraph(destination)
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(SECONDARY_COLOR))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(Border.NO_BORDER);

        routeTable.addCell(originCell);
        routeTable.addCell(arrowCell);
        routeTable.addCell(destinationCell);

        document.add(routeTable);
    }

    private void addPriceSection(Document document, Ticket ticket) {
        Paragraph priceLabel = new Paragraph("Montant / Price / المبلغ")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);

        Paragraph priceValue = new Paragraph(String.format("%.2f DH", ticket.getPrix().doubleValue()))
                .setFontSize(24)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(priceLabel);
        document.add(priceValue);
    }

    private void addQRCodeSection(Document document, Ticket ticket) throws WriterException, IOException {
        // Générer le QR code
        String qrData = qrCodeService.generateTicketData(
                ticket.getIdTicket(),
                ticket.getIdPassager(),
                "Route-" + ticket.getIdTrajet(),
                ticket.getDateAchat().format(DATE_FORMATTER)
        );

        byte[] qrCodeBytes = qrCodeService.generateQRCode(qrData);
        Image qrImage = new Image(ImageDataFactory.create(qrCodeBytes));
        qrImage.setWidth(200);
        qrImage.setHeight(200);
        qrImage.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Informations du billet
        Table ticketDetailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth()
                .setMarginTop(20)
                .setMarginBottom(20);

        // Colonne gauche: Détails
        Cell detailsCell = new Cell()
                .add(new Paragraph("Date d'achat: " + ticket.getDateAchat().format(DATE_FORMATTER))
                        .setFontSize(10))
                .add(new Paragraph("Heure: " + ticket.getDateAchat().format(TIME_FORMATTER))
                        .setFontSize(10))
                .add(new Paragraph("N° Ticket: " + ticket.getTicketNumber())
                        .setFontSize(10))
                .add(new Paragraph("Statut: " + ticket.getStatut())
                        .setFontSize(10)
                        .setBold()
                        .setFontColor(ticket.getStatut() == com.transport.ticket.model.TicketStatus.ACTIVE ? 
                                new DeviceRgb(40, 167, 69) : ColorConstants.GRAY))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(10);

        // Colonne droite: QR Code
        Cell qrCell = new Cell()
                .add(qrImage)
                .add(new Paragraph("Scannez pour valider")
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setItalic())
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);

        ticketDetailsTable.addCell(detailsCell);
        ticketDetailsTable.addCell(qrCell);

        document.add(ticketDetailsTable);
    }

    private void addFooter(Document document) {
        Paragraph footer = new Paragraph("NE PAS PLIER LE CODE À BARRE SVP / DO NOT FOLD THE QR CODE PLEASE")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic()
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(20);

        Paragraph footerNote = new Paragraph("Conservez ce billet pour tout le trajet • Keep this ticket for the entire journey")
                .setFontSize(7)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);

        document.add(footer);
        document.add(footerNote);
    }

    private Cell createLabelCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(9).setFontColor(ColorConstants.GRAY))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }

    private Cell createValueCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10).setBold())
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }
}

