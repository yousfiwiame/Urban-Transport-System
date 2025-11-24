package com.transport.notification.controller;

import com.transport.notification.dto.request.CreateTemplateRequest;
import com.transport.notification.dto.response.TemplateResponse;
import com.transport.notification.exception.TemplateNotFoundException;
import com.transport.notification.model.NotificationTemplate;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.repository.NotificationTemplateRepository;
import com.transport.notification.dto.mapper.NotificationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification template management.
 */
@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Template Management", description = "Notification template management endpoints")
public class TemplateController {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationMapper notificationMapper;

    @PostMapping
    @Operation(summary = "Create a notification template")
    public ResponseEntity<TemplateResponse> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request) {
        log.info("Creating template with code: {}", request.getTemplateCode());
        
        NotificationTemplate template = notificationMapper.toEntity(request);
        template = templateRepository.save(template);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationMapper.toResponse(template));
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<TemplateResponse> getTemplate(@PathVariable Integer templateId) {
        NotificationTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + templateId));
        
        return ResponseEntity.ok(notificationMapper.toResponse(template));
    }

    @GetMapping("/code/{templateCode}")
    @Operation(summary = "Get template by code")
    public ResponseEntity<TemplateResponse> getTemplateByCode(@PathVariable String templateCode) {
        NotificationTemplate template = templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found: " + templateCode));
        
        return ResponseEntity.ok(notificationMapper.toResponse(template));
    }

    @GetMapping("/channel/{channelType}")
    @Operation(summary = "Get templates by channel type")
    public ResponseEntity<List<TemplateResponse>> getTemplatesByChannel(
            @PathVariable ChannelType channelType) {
        List<NotificationTemplate> templates = templateRepository
                .findByChannelTypeAndIsActive(channelType, true);
        
        return ResponseEntity.ok(notificationMapper.toTemplateResponseList(templates));
    }

    @GetMapping
    @Operation(summary = "Get all active templates")
    public ResponseEntity<List<TemplateResponse>> getAllActiveTemplates() {
        List<NotificationTemplate> templates = templateRepository.findByIsActive(true);
        return ResponseEntity.ok(notificationMapper.toTemplateResponseList(templates));
    }
}

