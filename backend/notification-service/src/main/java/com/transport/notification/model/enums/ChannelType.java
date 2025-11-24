package com.transport.notification.model.enums;

/**
 * Enumeration representing the notification channel types.
 * 
 * <p>Supported channels:
 * <ul>
 *   <li>EMAIL - Email notifications</li>
 *   <li>SMS - Short Message Service notifications</li>
 *   <li>PUSH - Push notifications for mobile/web apps</li>
 *   <li>WEBHOOK - Webhook notifications for external integrations</li>
 * </ul>
 */
public enum ChannelType {
    EMAIL,
    SMS,
    PUSH,
    WEBHOOK
}

