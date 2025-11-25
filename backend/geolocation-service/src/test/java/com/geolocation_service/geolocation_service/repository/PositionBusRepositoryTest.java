package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.PositionBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest(excludeAutoConfiguration = {
        EurekaClientAutoConfiguration.class,
        KafkaAutoConfiguration.class,
        RedisAutoConfiguration.class,
        FeignAutoConfiguration.class
})
@ActiveProfiles("test")
class PositionBusRepositoryTest {

    @Autowired
    private PositionBusRepository positionBusRepository;

    @Autowired
    private BusRepository busRepository;

    private Bus testBus;
    private PositionBus position1;
    private PositionBus position2;

    @BeforeEach
    void setUp() {
        positionBusRepository.deleteAll();
        busRepository.deleteAll();

        // Créer un bus de test
        testBus = new Bus();
        testBus.setImmatriculation("TEST-001");
        testBus.setModele("Test Model");
        testBus.setMarque("Test Brand");
        testBus.setCapacite(50);
        testBus.setAnnee(2022);
        testBus.setStatut("EN_SERVICE");
        testBus = busRepository.save(testBus);

        // Créer des positions de test
        LocalDateTime now = LocalDateTime.now();

        position1 = new PositionBus();
        position1.setLatitude(33.5731);
        position1.setLongitude(-7.5898);
        position1.setVitesse(45.0);
        position1.setTimestamp(now.minusMinutes(10));
        position1.setBusId(1L); // Utiliser le nouveau système avec Long busId
        position1.setBus(testBus); // Garder pour compatibilité
        position1 = positionBusRepository.save(position1);

        position2 = new PositionBus();
        position2.setLatitude(33.5850);
        position2.setLongitude(-7.6000);
        position2.setVitesse(50.0);
        position2.setTimestamp(now);
        position2.setBusId(1L); // Utiliser le nouveau système avec Long busId
        position2.setBus(testBus); // Garder pour compatibilité
        position2 = positionBusRepository.save(position2);
    }

    @Test
    void testFindByBusId() {
        // When
        List<PositionBus> positions = positionBusRepository.findByBusId(1L);

        // Then
        assertThat(positions).hasSize(2);
    }

    @Test
    void testFindByBusIdOrderByTimestampDesc() {
        // When
        List<PositionBus> positions = positionBusRepository.findByBusIdOrderByTimestampDesc(1L);

        // Then
        assertThat(positions).hasSize(2);
        assertThat(positions.get(0).getTimestamp()).isAfter(positions.get(1).getTimestamp());
        assertThat(positions.get(0).getLatitude()).isEqualTo(33.5850);
    }

    @Test
    void testFindByBusIdAndTimestampBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(10);

        // When
        List<PositionBus> positions = positionBusRepository
                .findByBusIdAndTimestampBetweenOrderByTimestampAsc(1L, start, end);

        // Then
        assertThat(positions).hasSize(2);
        assertThat(positions.get(0).getTimestamp()).isBefore(positions.get(1).getTimestamp());
    }
}