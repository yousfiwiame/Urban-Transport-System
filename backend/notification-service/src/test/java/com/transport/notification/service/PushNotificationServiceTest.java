package com.transport.notification.service;

import com.transport.notification.util.TemplateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Push Notification Service Unit Tests")
class PushNotificationServiceTest {

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private com.transport.notification.service.PushNotificationServiceImpl pushService;

    @Test
    @DisplayName("Should send push notification successfully")
    void testSendPush_Success() {
        // Given
        String pushToken = "test-push-token";
        String title = "Test Title";
        String body = "Test Body";
        Map<String, String> data = Map.of("key", "value");

        // When
        boolean result = pushService.sendPush(pushToken, title, body, data);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should send push to multiple tokens")
    void testSendPushToMultiple_Success() {
        // Given
        List<String> pushTokens = List.of("token1", "token2", "token3");
        String title = "Test Title";
        String body = "Test Body";

        // When
        int successCount = pushService.sendPushToMultiple(pushTokens, title, body, null);

        // Then
        assertThat(successCount).isEqualTo(3);
    }

    @Test
    @DisplayName("Should send templated push notification")
    void testSendTemplatedPush_Success() {
        // Given
        String pushToken = "test-token";
        String titleTemplate = "Hello {{name}}";
        String bodyTemplate = "Your order {{orderId}} is ready";
        Map<String, String> variables = Map.of("name", "John", "orderId", "123");
        Map<String, String> data = null;

        when(templateProcessor.processTemplate(titleTemplate, variables))
                .thenReturn("Hello John");
        when(templateProcessor.processTemplate(bodyTemplate, variables))
                .thenReturn("Your order 123 is ready");

        // When
        boolean result = pushService.sendTemplatedPush(
                pushToken, titleTemplate, bodyTemplate, variables, data);

        // Then
        assertThat(result).isTrue();
        verify(templateProcessor).processTemplate(titleTemplate, variables);
        verify(templateProcessor).processTemplate(bodyTemplate, variables);
    }
}

