# Notification Service

Notification management microservice for the Urban Transport System.

## Overview

The Notification Service handles all notification-related functionality including:
- Email notifications
- SMS notifications  
- Push notifications
- Notification templates
- User notification preferences
- Event-driven notifications from other services

## Features

- **Multi-channel Support**: Email, SMS, and Push notifications
- **Template Engine**: Reusable notification templates with variable substitution
- **User Preferences**: Per-user notification channel preferences and do-not-disturb settings
- **Event-Driven**: Consumes events from other services via Kafka
- **Retry Logic**: Automatic retry for failed notifications
- **Audit Logging**: Complete audit trail of all notification actions

## Technology Stack

- **Framework**: Spring Boot 3.4.4
- **Language**: Java 17
- **Database**: PostgreSQL
- **Message Broker**: Apache Kafka
- **Email**: JavaMail (SMTP)
- **Build Tool**: Maven

## Database Schema

The service uses the following main tables:
- `notification_template` - Notification templates
- `notification_event` - Incoming events from other services
- `user_notification_preference` - User notification preferences
- `notification` - Main notification records
- `notification_log` - Audit logs
- `notification_channel` - Channel-specific delivery tracking

## API Endpoints

### Notifications
- `POST /api/notifications` - Send a notification
- `GET /api/notifications/users/{userId}` - Get user notifications
- `GET /api/notifications/users/{userId}/status/{status}` - Get notifications by status
- `PUT /api/notifications/{notificationId}/read` - Mark as read
- `GET /api/notifications/users/{userId}/unread/count` - Get unread count

### Templates
- `POST /api/templates` - Create template
- `GET /api/templates/{templateId}` - Get template by ID
- `GET /api/templates/code/{templateCode}` - Get template by code
- `GET /api/templates/channel/{channelType}` - Get templates by channel
- `GET /api/templates` - Get all active templates

### Preferences
- `GET /api/preferences/users/{userId}` - Get user preferences
- `PUT /api/preferences/users/{userId}` - Update user preferences

## Kafka Topics Consumed

- `user-created-events`
- `user-updated-events`
- `user-deleted-events`
- `subscription-created`
- `subscription-renewed`
- `subscription-cancelled`
- `ticket-purchased-events`
- `schedule-updated-events`
- `route-changed-events`
- `location-updated-events`
- `bus-arrived-events`

## Configuration

Key configuration properties:
- `spring.mail.*` - Email server configuration
- `notification.sms.enabled` - Enable/disable SMS
- `notification.push.enabled` - Enable/disable Push notifications
- `spring.kafka.bootstrap-servers` - Kafka broker addresses

## Running the Service

```bash
# Using Maven
mvn spring-boot:run -pl notification-service

# Using Docker
docker build -t notification-service .
docker run -p 8086:8086 notification-service
```

## Health Check

The service exposes health check endpoint:
- `GET /actuator/health`

## Swagger Documentation

API documentation is available at:
- `http://localhost:8086/swagger-ui.html`

