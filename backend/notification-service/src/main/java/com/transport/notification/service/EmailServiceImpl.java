package com.transport.notification.service;

import com.transport.notification.util.TemplateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

/**
 * Implementation of EmailService for sending email notifications.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateProcessor templateProcessor;

    @Value("${spring.mail.from:notifications@transport.com}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Check if body contains HTML
            if (body.contains("<html") || body.contains("<div") || body.contains("<p>")) {
                return sendHtmlEmail(to, subject, body);
            } else {
                return sendPlainTextEmail(to, subject, body);
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean sendTemplatedEmail(String to, String subject, String templateBody, Map<String, String> variables) {
        String processedBody = templateProcessor.processTemplate(templateBody, variables);
        return sendEmail(to, subject, processedBody);
    }

    private boolean sendPlainTextEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send plain text email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    private boolean sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML
            mailSender.send(message);
            log.info("HTML email sent successfully to {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}

