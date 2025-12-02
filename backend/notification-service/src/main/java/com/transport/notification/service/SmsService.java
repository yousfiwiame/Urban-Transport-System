package com.transport.notification.service;

/**
 * Service interface for sending SMS notifications.
 */
public interface SmsService {

    /**
     * Sends an SMS notification.
     * 
     * @param phoneNumber recipient phone number
     * @param message SMS message text
     * @return true if SMS was sent successfully, false otherwise
     */
    boolean sendSms(String phoneNumber, String message);

    /**
     * Sends an SMS using a template.
     * 
     * @param phoneNumber recipient phone number
     * @param templateMessage template message with variables
     * @param variables map of variables to replace in template
     * @return true if SMS was sent successfully, false otherwise
     */
    boolean sendTemplatedSms(String phoneNumber, String templateMessage, java.util.Map<String, String> variables);
}

