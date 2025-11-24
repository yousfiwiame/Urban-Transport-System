package com.transport.notification.service;

import com.transport.notification.util.TemplateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of SmsService for sending SMS notifications.
 * 
 * <p>This is a placeholder implementation. In production, integrate with
 * an SMS provider like Twilio, AWS SNS, or similar service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "notification.sms.enabled", havingValue = "true", matchIfMissing = false)
public class SmsServiceImpl implements SmsService {

    private final TemplateProcessor templateProcessor;

    @Value("${notification.sms.provider:mock}")
    private String smsProvider;

    @Override
    public boolean sendSms(String phoneNumber, String message) {
        log.info("Sending SMS to {} via provider {}: {}", phoneNumber, smsProvider, message);
        
        // TODO: Integrate with actual SMS provider (Twilio, AWS SNS, etc.)
        // For now, this is a mock implementation
        try {
            // Simulate SMS sending
            Thread.sleep(100);
            log.info("SMS sent successfully to {}", phoneNumber);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SMS sending interrupted for {}", phoneNumber);
            return false;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendTemplatedSms(String phoneNumber, String templateMessage, Map<String, String> variables) {
        String processedMessage = templateProcessor.processTemplate(templateMessage, variables);
        return sendSms(phoneNumber, processedMessage);
    }
}

