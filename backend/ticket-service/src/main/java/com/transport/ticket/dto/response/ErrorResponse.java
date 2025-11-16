package com.transport.ticket.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de réponse en cas d'erreur
 * Renvoyé par le GlobalExceptionHandler
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private Integer status; // 400, 404, 500, etc.

    private String error; // "Bad Request", "Not Found", etc.

    private String message; // Message d'erreur détaillé

    private String path; // URL qui a causé l'erreur

    private List<String> validationErrors; // Erreurs de validation (optionnel)
}