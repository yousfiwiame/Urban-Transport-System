package com.transport.notification.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for sending push notifications.
 */
public interface PushNotificationService {

    /**
     * Sends a push notification to a single device.
     * 
     * @param pushToken device push token
     * @param title notification title
     * @param body notification body
     * @param data optional data payload
     * @return true if push notification was sent successfully, false otherwise
     */
    boolean sendPush(String pushToken, String title, String body, Map<String, String> data);

    /**
     * Sends push notifications to multiple devices.
     * 
     * @param pushTokens list of device push tokens
     * @param title notification title
     * @param body notification body
     * @param data optional data payload
     * @return number of successfully sent notifications
     */
    int sendPushToMultiple(List<String> pushTokens, String title, String body, Map<String, String> data);

    /**
     * Sends a push notification using a template.
     * 
     * @param pushToken device push token
     * @param titleTemplate template for title with variables
     * @param bodyTemplate template for body with variables
     * @param variables map of variables to replace in templates
     * @param data optional data payload
     * @return true if push notification was sent successfully, false otherwise
     */
    boolean sendTemplatedPush(String pushToken, String titleTemplate, String bodyTemplate, 
                             Map<String, String> variables, Map<String, String> data);
}

