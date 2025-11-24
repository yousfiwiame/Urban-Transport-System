package com.transport.notification.service;

import com.transport.notification.util.TemplateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email Service Unit Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private com.transport.notification.service.EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", "notifications@transport.com");
    }

    @Test
    @DisplayName("Should send plain text email successfully")
    void testSendEmail_PlainText_Success() {
        // Given
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "Plain text body";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        boolean result = emailService.sendEmail(to, subject, body);

        // Then
        assertThat(result).isTrue();
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send HTML email successfully")
    void testSendEmail_HTML_Success() {
        // Given
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "<html><body><p>HTML content</p></body></html>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(to, subject, body);

        // Then
        assertThat(result).isTrue();
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should send templated email successfully")
    void testSendTemplatedEmail_Success() {
        // Given
        String to = "user@example.com";
        String subject = "Test Subject";
        String templateBody = "Hello {{name}}";
        Map<String, String> variables = Map.of("name", "John");
        String processedBody = "Hello John";

        when(templateProcessor.processTemplate(templateBody, variables))
                .thenReturn(processedBody);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        boolean result = emailService.sendTemplatedEmail(to, subject, templateBody, variables);

        // Then
        assertThat(result).isTrue();
        verify(templateProcessor).processTemplate(templateBody, variables);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should return false when email sending fails")
    void testSendEmail_Failure() {
        // Given
        String to = "user@example.com";
        String subject = "Test Subject";
        String body = "Plain text body";

        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        boolean result = emailService.sendEmail(to, subject, body);

        // Then
        assertThat(result).isFalse();
    }
}

