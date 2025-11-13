package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.PositionBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(locations = "classpath:application-test.properties")
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
        position1.setBus(testBus);
        position1 = positionBusRepository.save(position1);

        position2 = new PositionBus();
        position2.setLatitude(33.5850);
        position2.setLongitude(-7.6000);
        position2.setVitesse(50.0);
        position2.setTimestamp(now);
        position2.setBus(testBus);
        position2 = positionBusRepository.save(position2);
    }

    @Test
    void testFindByBusIdBus() {
        // When
        List<PositionBus> positions = positionBusRepository.findByBusIdBus(testBus.getIdBus());

        // Then
        assertThat(positions).hasSize(2);
    }

    @Test
    void testFindByBusIdBusOrderByTimestampDesc() {
        // When
        List<PositionBus> positions = positionBusRepository.findByBusIdBusOrderByTimestampDesc(testBus.getIdBus());

        // Then
        assertThat(positions).hasSize(2);
        assertThat(positions.get(0).getTimestamp()).isAfter(positions.get(1).getTimestamp());
        assertThat(positions.get(0).getLatitude()).isEqualTo(33.5850);
    }

    @Test
    void testFindByBusIdBusAndTimestampBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(10);

        // When
        List<PositionBus> positions = positionBusRepository
                .findByBusIdBusAndTimestampBetweenOrderByTimestampAsc(testBus.getIdBus(), start, end);

        // Then
        assertThat(positions).hasSize(2);
        assertThat(positions.get(0).getTimestamp()).isBefore(positions.get(1).getTimestamp());
    }
}