package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.dto.TrajetInfoDTO;
import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.model.Direction;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.BusRepository;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrajetInfoServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private PositionBusRepository positionBusRepository;

    @InjectMocks
    private TrajetInfoService trajetInfoService;

    private Bus testBus;
    private LigneBus testLigne;
    private Direction testDirection;
    private List<PositionBus> testPositions;

    @BeforeEach
    void setUp() {
        // Setup ligne
        testLigne = new LigneBus();
        testLigne.setIdLigne("ligne-1");
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setCouleur("#FF5733");

        // Setup direction
        testDirection = new Direction();
        testDirection.setIdDirection("dir-1");
        testDirection.setNomDirection("Aller");
        testDirection.setPointDepart("Casa Port");
        testDirection.setPointArrivee("Ain Diab");

        // Setup bus
        testBus = new Bus();
        testBus.setIdBus("bus-1");
        testBus.setImmatriculation("A-12345-B");
        testBus.setLigneActuelle(testLigne);
        testBus.setDirectionActuelle(testDirection);

        // Setup positions
        testPositions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 5; i++) {
            PositionBus pos = new PositionBus();
            pos.setIdPosition("pos-" + i);
            pos.setLatitude(33.5731 + (i * 0.001));
            pos.setLongitude(-7.5898 + (i * 0.001));
            pos.setVitesse(40.0 + i);
            pos.setTimestamp(now.minusMinutes(5 * i));
            pos.setBusId(1L); // Utiliser le nouveau système avec Long busId
            pos.setBus(testBus); // Garder pour compatibilité
            testPositions.add(pos);
        }
    }

    @Test
    void testGetTrajetInfo() {
        // Given
        // Le service appelle busRepository.findById(busId) avec busId = "1"
        when(busRepository.findById("1")).thenReturn(Optional.of(testBus));
        // Le service convertit String en Long, donc on mock avec Long
        when(positionBusRepository.findByBusIdOrderByTimestampDesc(1L))
                .thenReturn(testPositions);
        when(positionBusRepository.findByBusIdAndTimestampBetweenOrderByTimestampAsc(
                any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(testPositions);

        // When
        TrajetInfoDTO result = trajetInfoService.getTrajetInfo("1"); // Passer "1" qui sera converti en 1L

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getIdBus()).isEqualTo("bus-1");
        assertThat(result.getImmatriculation()).isEqualTo("A-12345-B");
        assertThat(result.getLigne()).isNotNull();
        assertThat(result.getLigne().getNumeroLigne()).isEqualTo("15");
        assertThat(result.getDirection()).isNotNull();
        assertThat(result.getLatitudeActuelle()).isEqualTo(33.5731);
        assertThat(result.getDistanceParcourue()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testGetTrajetInfoBusNotFound() {
        // Given
        when(busRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> trajetInfoService.getTrajetInfo("non-existent"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bus non trouvé");
    }

    @Test
    void testGetTrajetInfoNoPosition() {
        // Given
        // Le service appelle busRepository.findById(busId) avec busId = "1"
        when(busRepository.findById("1")).thenReturn(Optional.of(testBus));
        // Le service convertit String en Long, donc on mock avec Long
        when(positionBusRepository.findByBusIdOrderByTimestampDesc(1L))
                .thenReturn(new ArrayList<>());

        // When & Then
        assertThatThrownBy(() -> trajetInfoService.getTrajetInfo("1")) // Passer "1" qui sera converti en 1L
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Aucune position trouvée");
    }
}