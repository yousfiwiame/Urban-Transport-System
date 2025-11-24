package com.transport.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.notification.dto.request.CreateTemplateRequest;
import com.transport.notification.dto.response.TemplateResponse;
import com.transport.notification.exception.TemplateNotFoundException;
import com.transport.notification.model.NotificationTemplate;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.repository.NotificationTemplateRepository;
import com.transport.notification.dto.mapper.NotificationMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateController.class)
@DisplayName("Template Controller Tests")
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationTemplateRepository templateRepository;

    @MockBean
    private NotificationMapper notificationMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create template successfully")
    void testCreateTemplate_Success() throws Exception {
        // Given
        CreateTemplateRequest request = CreateTemplateRequest.builder()
                .templateCode("test-template")
                .channelType(ChannelType.EMAIL)
                .subject("Test Subject")
                .bodyTemplate("Test Body")
                .isActive(true)
                .build();

        NotificationTemplate template = NotificationTemplate.builder()
                .templateId(1)
                .templateCode("test-template")
                .channelType(ChannelType.EMAIL)
                .subject("Test Subject")
                .bodyTemplate("Test Body")
                .isActive(true)
                .build();

        TemplateResponse response = TemplateResponse.builder()
                .templateId(1)
                .templateCode("test-template")
                .channelType(ChannelType.EMAIL)
                .build();

        when(notificationMapper.toEntity(any(CreateTemplateRequest.class)))
                .thenReturn(template);
        when(templateRepository.save(any(NotificationTemplate.class)))
                .thenReturn(template);
        when(notificationMapper.toResponse(any(NotificationTemplate.class)))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.templateId").value(1))
                .andExpect(jsonPath("$.templateCode").value("test-template"));
    }

    @Test
    @DisplayName("Should get template by ID")
    void testGetTemplateById_Success() throws Exception {
        // Given
        NotificationTemplate template = NotificationTemplate.builder()
                .templateId(1)
                .templateCode("test-template")
                .channelType(ChannelType.EMAIL)
                .build();

        TemplateResponse response = TemplateResponse.builder()
                .templateId(1)
                .templateCode("test-template")
                .build();

        when(templateRepository.findById(1))
                .thenReturn(Optional.of(template));
        when(notificationMapper.toResponse(template))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/templates/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateId").value(1));
    }

    @Test
    @DisplayName("Should return 404 when template not found")
    void testGetTemplateById_NotFound() throws Exception {
        // Given
        when(templateRepository.findById(999))
                .thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/templates/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get template by code")
    void testGetTemplateByCode_Success() throws Exception {
        // Given
        NotificationTemplate template = NotificationTemplate.builder()
                .templateId(1)
                .templateCode("test-template")
                .build();

        TemplateResponse response = TemplateResponse.builder()
                .templateId(1)
                .templateCode("test-template")
                .build();

        when(templateRepository.findByTemplateCode("test-template"))
                .thenReturn(Optional.of(template));
        when(notificationMapper.toResponse(template))
                .thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/templates/code/test-template"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateCode").value("test-template"));
    }

    @Test
    @DisplayName("Should get templates by channel type")
    void testGetTemplatesByChannel_Success() throws Exception {
        // Given
        NotificationTemplate template = NotificationTemplate.builder()
                .templateId(1)
                .templateCode("email-template")
                .channelType(ChannelType.EMAIL)
                .isActive(true)
                .build();

        TemplateResponse response = TemplateResponse.builder()
                .templateId(1)
                .templateCode("email-template")
                .channelType(ChannelType.EMAIL)
                .build();

        when(templateRepository.findByChannelTypeAndIsActive(ChannelType.EMAIL, true))
                .thenReturn(List.of(template));
        when(notificationMapper.toTemplateResponseList(anyList()))
                .thenReturn(List.of(response));

        // When/Then
        mockMvc.perform(get("/api/templates/channel/EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].channelType").value("EMAIL"));
    }
}

