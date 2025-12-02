package com.transport.notification.service;

import com.transport.notification.util.TemplateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SMS Service Unit Tests")
class SmsServiceTest {

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private com.transport.notification.service.SmsServiceImpl smsService;

    @Test
    @DisplayName("Should send SMS successfully")
    void testSendSms_Success() {
        // Given
        String phoneNumber = "+1234567890";
        String message = "Test SMS message";

        // When
        boolean result = smsService.sendSms(phoneNumber, message);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should send templated SMS successfully")
    void testSendTemplatedSms_Success() {
        // Given
        String phoneNumber = "+1234567890";
        String templateMessage = "Hello {{name}}";
        Map<String, String> variables = Map.of("name", "John");
        String processedMessage = "Hello John";

        when(templateProcessor.processTemplate(templateMessage, variables))
                .thenReturn(processedMessage);

        // When
        boolean result = smsService.sendTemplatedSms(phoneNumber, templateMessage, variables);

        // Then
        assertThat(result).isTrue();
        verify(templateProcessor).processTemplate(templateMessage, variables);
    }
}

