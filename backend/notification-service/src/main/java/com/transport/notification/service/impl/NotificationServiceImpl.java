package com.transport.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.notification.dto.mapper.NotificationMapper;
import com.transport.notification.dto.request.SendNotificationRequest;
import com.transport.notification.dto.response.NotificationResponse;
import com.transport.notification.exception.NotificationNotFoundException;
import com.transport.notification.model.*;
import com.transport.notification.model.enums.ChannelStatus;
import com.transport.notification.model.enums.ChannelType;
import com.transport.notification.model.enums.NotificationStatus;
import com.transport.notification.model.enums.ProcessingStatus;
import com.transport.notification.repository.*;
import com.transport.notification.service.EmailService;
import com.transport.notification.service.NotificationService;
import com.transport.notification.service.PushNotificationService;
import com.transport.notification.service.SmsService;
import com.transport.notification.util.TemplateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of NotificationService for managing notifications.
 * 
 * <p>This service orchestrates notification creation, user preference checking,
 * channel selection, delivery, and retry logic.
 */
@Service
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationEventRepository eventRepository;
    private final NotificationChannelRepository channelRepository;
    private final NotificationLogRepository logRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    private final TemplateProcessor templateProcessor;
    private final ObjectMapper objectMapper;
    
    // Optional services - may not be available if not configured
    private SmsService smsService;
    private PushNotificationService pushService;

    @Autowired
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            NotificationTemplateRepository templateRepository,
            NotificationPreferenceRepository preferenceRepository,
            NotificationEventRepository eventRepository,
            NotificationChannelRepository channelRepository,
            NotificationLogRepository logRepository,
            NotificationMapper notificationMapper,
            EmailService emailService,
            TemplateProcessor templateProcessor,
            ObjectMapper objectMapper,
            @Autowired(required = false) SmsService smsService,
            @Autowired(required = false) PushNotificationService pushService) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
        this.preferenceRepository = preferenceRepository;
        this.eventRepository = eventRepository;
        this.channelRepository = channelRepository;
        this.logRepository = logRepository;
        this.notificationMapper = notificationMapper;
        this.emailService = emailService;
        this.templateProcessor = templateProcessor;
        this.objectMapper = objectMapper;
        this.smsService = smsService;
        this.pushService = pushService;
    }

    @Override
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending notification to user: {}", request.getUserId());

        // 1. Get or create user preferences
        UserNotificationPreference preference = getOrCreatePreference(request.getUserId());

        // 2. Check if user is in do-not-disturb period
        if (isInDoNotDisturbPeriod(preference)) {
            log.info("User {} is in do-not-disturb period, scheduling notification", request.getUserId());
            // Schedule for later
            request.setScheduledAt(calculateNextAvailableTime(preference));
        }

        // 3. Process template if provided
        String title = request.getTitle();
        String messageBody = request.getMessageBody();
        NotificationTemplate template = null;

        if (request.getTemplateCode() != null) {
            template = templateRepository.findByTemplateCode(request.getTemplateCode())
                    .orElseThrow(() -> new RuntimeException("Template not found: " + request.getTemplateCode()));
            
            if (template.getSubject() != null) {
                title = templateProcessor.processTemplate(template.getSubject(), request.getTemplateVariables());
            }
            messageBody = templateProcessor.processTemplate(template.getBodyTemplate(), request.getTemplateVariables());
        }

        // 4. Create notification entity
        Notification notification = notificationMapper.toEntity(request);
        notification.setUserId(request.getUserId());
        notification.setTitle(title);
        notification.setMessageBody(messageBody);
        notification.setTemplate(template);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setScheduledAt(request.getScheduledAt());
        notification = notificationRepository.save(notification);

        // 5. Create notification channels based on user preferences
        createNotificationChannels(notification, preference, request.getChannelType());

        // 6. Log creation
        createLog(notification, "NOTIFICATION_CREATED", Map.of("channelType", request.getChannelType().name()));

        // 7. Send immediately if not scheduled
        if (notification.getScheduledAt() == null || notification.getScheduledAt().isBefore(OffsetDateTime.now())) {
            sendNotificationChannels(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Integer userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotificationsByStatus(Integer userId, NotificationStatus status, Pageable pageable) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public NotificationResponse markAsRead(Integer notificationId, Integer userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Notification does not belong to user");
        }

        if (notification.getReadAt() == null) {
            notification.setReadAt(OffsetDateTime.now());
            notification.setStatus(NotificationStatus.READ);
            notification = notificationRepository.save(notification);
            createLog(notification, "NOTIFICATION_READ", null);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Scheduled(fixedDelay = 60000) // Run every minute
    public void processPendingNotifications() {
        log.debug("Processing pending notifications");
        
        List<Notification> pendingNotifications = notificationRepository.findPendingNotificationsReadyToSend(
                NotificationStatus.PENDING, 
                OffsetDateTime.now()
        );

        for (Notification notification : pendingNotifications) {
            try {
                sendNotificationChannels(notification);
            } catch (Exception e) {
                log.error("Failed to process notification {}: {}", notification.getNotificationId(), e.getMessage(), e);
            }
        }

        // Process retries
        processRetries();
    }

    private UserNotificationPreference getOrCreatePreference(Integer userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserNotificationPreference newPreference = UserNotificationPreference.builder()
                            .userId(userId)
                            .emailEnabled(true)
                            .smsEnabled(false)
                            .pushEnabled(false)
                            .build();
                    return preferenceRepository.save(newPreference);
                });
    }

    private boolean isInDoNotDisturbPeriod(UserNotificationPreference preference) {
        if (preference.getDoNotDisturbStart() == null || preference.getDoNotDisturbEnd() == null) {
            return false;
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime start = preference.getDoNotDisturbStart();
        OffsetDateTime end = preference.getDoNotDisturbEnd();

        // Handle case where do-not-disturb spans midnight
        if (start.isBefore(end)) {
            return now.isAfter(start) && now.isBefore(end);
        } else {
            // Spans midnight
            return now.isAfter(start) || now.isBefore(end);
        }
    }

    private OffsetDateTime calculateNextAvailableTime(UserNotificationPreference preference) {
        if (preference.getDoNotDisturbEnd() == null) {
            return OffsetDateTime.now();
        }
        return preference.getDoNotDisturbEnd().plusMinutes(1);
    }

    private void createNotificationChannels(Notification notification, UserNotificationPreference preference, ChannelType requestedChannel) {
        // Always create explicitly requested channel via API
        // Explicit API requests take precedence over user preferences
        createChannel(notification, requestedChannel);
    }

    private NotificationChannel createChannel(Notification notification, ChannelType channelType) {
        NotificationChannel channel = NotificationChannel.builder()
                .notification(notification)
                .channelType(channelType)
                .channelStatus(ChannelStatus.PENDING)
                .retryCount(0)
                .build();
        NotificationChannel savedChannel = channelRepository.save(channel);
        // Add to notification's channels list for immediate access
        notification.getChannels().add(savedChannel);
        return savedChannel;
    }

    private void sendNotificationChannels(Notification notification) {
        notification.setStatus(NotificationStatus.SENDING);
        notification = notificationRepository.save(notification);

        List<NotificationChannel> channels = channelRepository.findByNotification_NotificationId(notification.getNotificationId());

        for (NotificationChannel channel : channels) {
            try {
                sendViaChannel(notification, channel);
            } catch (Exception e) {
                log.error("Failed to send notification {} via channel {}: {}", 
                        notification.getNotificationId(), channel.getChannelType(), e.getMessage(), e);
                handleChannelFailure(channel, e);
            }
        }

        // Update notification status
        boolean allSuccess = channels.stream()
                .allMatch(c -> c.getChannelStatus() == ChannelStatus.SUCCESS);
        boolean anySuccess = channels.stream()
                .anyMatch(c -> c.getChannelStatus() == ChannelStatus.SUCCESS);

        if (allSuccess) {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(OffsetDateTime.now());
        } else if (anySuccess) {
            notification.setStatus(NotificationStatus.SENT); // Partial success
            notification.setSentAt(OffsetDateTime.now());
        } else {
            notification.setStatus(NotificationStatus.FAILED);
        }
        notificationRepository.save(notification);
    }

    private void sendViaChannel(Notification notification, NotificationChannel channel) {
        channel.setChannelStatus(ChannelStatus.SENDING);
        channel.setLastAttemptAt(OffsetDateTime.now());
        channel = channelRepository.save(channel);

        boolean success = false;
        UserNotificationPreference preference = getOrCreatePreference(notification.getUserId());

        switch (channel.getChannelType()) {
            case EMAIL:
                if (preference.getEmailAddress() != null) {
                    success = emailService.sendEmail(preference.getEmailAddress(), notification.getTitle(), notification.getMessageBody());
                }
                break;
            case SMS:
                if (preference.getPhoneNumber() != null && smsService != null) {
                    success = smsService.sendSms(preference.getPhoneNumber(), notification.getMessageBody());
                } else {
                    log.warn("SMS service not available or phone number not set");
                    success = false;
                }
                break;
            case PUSH:
                if (pushService != null) {
                    // TODO: Parse push tokens from JSON
                    // For now, skip push if no tokens
                    success = false; // Placeholder
                } else {
                    log.warn("Push notification service not available");
                    success = false;
                }
                break;
            default:
                log.warn("Unsupported channel type: {}", channel.getChannelType());
        }

        if (success) {
            channel.setChannelStatus(ChannelStatus.SUCCESS);
            createLog(notification, "CHANNEL_SUCCESS", Map.of("channelType", channel.getChannelType().name()));
        } else {
            throw new RuntimeException("Channel delivery failed");
        }
        channelRepository.save(channel);
    }

    private void handleChannelFailure(NotificationChannel channel, Exception e) {
        channel.setChannelStatus(ChannelStatus.FAILED);
        channel.setErrorCode("DELIVERY_FAILED");
        channel.setErrorMessage(e.getMessage());
        channel.setRetryCount(channel.getRetryCount() + 1);

        // Schedule retry if retry count < max
        if (channel.getRetryCount() < 3) {
            channel.setChannelStatus(ChannelStatus.RETRYING);
            channel.setNextRetryAt(OffsetDateTime.now().plusMinutes(5 * channel.getRetryCount()));
        }

        channelRepository.save(channel);
    }

    private void processRetries() {
        List<NotificationChannel> retryChannels = channelRepository.findChannelsReadyForRetry(
                ChannelStatus.RETRYING,
                OffsetDateTime.now()
        );

        for (NotificationChannel channel : retryChannels) {
            try {
                Notification notification = channel.getNotification();
                sendViaChannel(notification, channel);
            } catch (Exception e) {
                log.error("Retry failed for channel {}: {}", channel.getChannelId(), e.getMessage());
                handleChannelFailure(channel, e);
            }
        }
    }

    private void createLog(Notification notification, String action, Map<String, String> metadata) {
        String metadataJson = null;
        if (metadata != null && !metadata.isEmpty()) {
            try {
                metadataJson = objectMapper.writeValueAsString(metadata);
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize metadata to JSON: {}", e.getMessage());
                // Fallback to null if serialization fails
            }
        }
        
        NotificationLog log = NotificationLog.builder()
                .notification(notification)
                .action(action)
                .metadata(metadataJson)
                .build();
        logRepository.save(log);
    }
}

