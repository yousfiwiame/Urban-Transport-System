package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionBusServiceTest {

    @Mock
    private PositionBusRepository positionBusRepository;

    @InjectMocks
    private PositionBusService positionBusService;

    private PositionBus testPosition;
    private Bus testBus;

    @BeforeEach
    void setUp() {
        testBus = new Bus();
        testBus.setIdBus("bus-1");
        testBus.setImmatriculation("A-12345-B");

        testPosition = new PositionBus();
        testPosition.setIdPosition("pos-1");
        testPosition.setLatitude(33.5731);
        testPosition.setLongitude(-7.5898);
        testPosition.setAltitude(50.0);
        testPosition.setPrecision(10.0);
        testPosition.setVitesse(45.0);
        testPosition.setDirection(90.0);
        testPosition.setTimestamp(LocalDateTime.now());
        testPosition.setBus(testBus);
    }

    @Test
    void testGetAllPositions() {
        // Given
        List<PositionBus> positions = Arrays.asList(testPosition);
        when(positionBusRepository.findAll()).thenReturn(positions);

        // When
        List<PositionBus> result = positionBusService.getAllPositions();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLatitude()).isEqualTo(33.5731);
        verify(positionBusRepository, times(1)).findAll();
    }

    @Test
    void testGetPositionsByBusId() {
        // Given
        List<PositionBus> positions = Arrays.asList(testPosition);
        when(positionBusRepository.findByBusIdBus("bus-1")).thenReturn(positions);

        // When
        List<PositionBus> result = positionBusService.getPositionsByBusId("bus-1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBus().getIdBus()).isEqualTo("bus-1");
        verify(positionBusRepository, times(1)).findByBusIdBus("bus-1");
    }

    @Test
    void testAddPosition() {
        // Given
        when(positionBusRepository.save(any(PositionBus.class))).thenReturn(testPosition);

        // When
        PositionBus result = positionBusService.addPosition(testPosition);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLatitude()).isEqualTo(33.5731);
        assertThat(result.getVitesse()).isEqualTo(45.0);
        verify(positionBusRepository, times(1)).save(testPosition);
    }
}