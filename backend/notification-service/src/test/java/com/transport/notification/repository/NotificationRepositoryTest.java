package com.transport.notification.repository;

import com.transport.notification.model.Notification;
import com.transport.notification.model.enums.NotificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Notification Repository Tests")
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    @DisplayName("Should find notifications by user ID")
    void testFindByUserId() {
        // Given
        Notification notification1 = Notification.builder()
                .userId(1)
                .title("Test 1")
                .messageBody("Body 1")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification notification2 = Notification.builder()
                .userId(1)
                .title("Test 2")
                .messageBody("Body 2")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification notification3 = Notification.builder()
                .userId(2)
                .title("Test 3")
                .messageBody("Body 3")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.persist(notification3);
        entityManager.flush();

        // When
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> result = notificationRepository.findByUserIdOrderByCreatedAtDesc(1, pageable);

        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(Notification::getUserId).containsOnly(1);
    }

    @Test
    @DisplayName("Should find notifications by status")
    void testFindByStatus() {
        // Given
        Notification pending1 = Notification.builder()
                .userId(1)
                .title("Pending 1")
                .messageBody("Body")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification pending2 = Notification.builder()
                .userId(2)
                .title("Pending 2")
                .messageBody("Body")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification sent = Notification.builder()
                .userId(1)
                .title("Sent")
                .messageBody("Body")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now())
                .build();

        entityManager.persist(pending1);
        entityManager.persist(pending2);
        entityManager.persist(sent);
        entityManager.flush();

        // When
        List<Notification> result = notificationRepository.findByStatus(NotificationStatus.PENDING);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Notification::getStatus).containsOnly(NotificationStatus.PENDING);
    }

    @Test
    @DisplayName("Should count unread notifications")
    void testCountUnreadByUserId() {
        // Given
        Notification read = Notification.builder()
                .userId(1)
                .title("Read")
                .messageBody("Body")
                .status(NotificationStatus.READ)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification pending = Notification.builder()
                .userId(1)
                .title("Pending")
                .messageBody("Body")
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .build();

        Notification sent = Notification.builder()
                .userId(1)
                .title("Sent")
                .messageBody("Body")
                .status(NotificationStatus.SENT)
                .createdAt(OffsetDateTime.now())
                .build();

        entityManager.persist(read);
        entityManager.persist(pending);
        entityManager.persist(sent);
        entityManager.flush();

        // When
        Long count = notificationRepository.countUnreadByUserId(1);

        // Then
        assertThat(count).isEqualTo(2L); // PENDING and SENT are not READ
    }
}

