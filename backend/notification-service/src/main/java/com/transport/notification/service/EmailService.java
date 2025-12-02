package com.transport.notification.service;

/**
 * Service interface for sending email notifications.
 */
public interface EmailService {

    /**
     * Sends an email notification.
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param body email body (can be HTML)
     * @return true if email was sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String body);

    /**
     * Sends an email using a template.
     * 
     * @param to recipient email address
     * @param subject email subject
     * @param templateBody template body with variables
     * @param variables map of variables to replace in template
     * @return true if email was sent successfully, false otherwise
     */
    boolean sendTemplatedEmail(String to, String subject, String templateBody, java.util.Map<String, String> variables);
}

