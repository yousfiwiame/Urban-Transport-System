package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    private Bus testBus;
    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        testLigne = new LigneBus();
        testLigne.setIdLigne("ligne-1");
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");

        testBus = new Bus();
        testBus.setIdBus("bus-1");
        testBus.setImmatriculation("A-12345-B");
        testBus.setModele("Mercedes Citaro");
        testBus.setMarque("Mercedes");
        testBus.setCapacite(80);
        testBus.setAnnee(2020);
        testBus.setStatut("EN_SERVICE");
        testBus.setLigneActuelle(testLigne);
    }

    @Test
    void testGetAllBuses() {
        // Given
        List<Bus> buses = Arrays.asList(testBus);
        when(busRepository.findAll()).thenReturn(buses);

        // When
        List<Bus> result = busService.getAllBuses();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImmatriculation()).isEqualTo("A-12345-B");
        verify(busRepository, times(1)).findAll();
    }

    @Test
    void testGetBusById() {
        // Given
        when(busRepository.findById("bus-1")).thenReturn(Optional.of(testBus));

        // When
        Optional<Bus> result = busService.getBusById("bus-1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getImmatriculation()).isEqualTo("A-12345-B");
        verify(busRepository, times(1)).findById("bus-1");
    }

    @Test
    void testGetBusesByLigne() {
        // Given
        List<Bus> buses = Arrays.asList(testBus);
        when(busRepository.findByLigneActuelleIdLigne("ligne-1")).thenReturn(buses);

        // When
        List<Bus> result = busService.getBusesByLigne("ligne-1");

        // Then
        assertThat(result).hasSize(1);
        verify(busRepository, times(1)).findByLigneActuelleIdLigne("ligne-1");
    }

    @Test
    void testCreateBus() {
        // Given
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        // When
        Bus result = busService.createBus(testBus);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getImmatriculation()).isEqualTo("A-12345-B");
        verify(busRepository, times(1)).save(testBus);
    }

    @Test
    void testUpdateBus() {
        // Given
        Bus updatedBus = new Bus();
        updatedBus.setImmatriculation("A-99999-Z");
        updatedBus.setModele("Iveco Urbanway");
        updatedBus.setMarque("Iveco");
        updatedBus.setCapacite(75);
        updatedBus.setAnnee(2021);
        updatedBus.setStatut("MAINTENANCE");

        when(busRepository.findById("bus-1")).thenReturn(Optional.of(testBus));
        when(busRepository.save(any(Bus.class))).thenReturn(testBus);

        // When
        Bus result = busService.updateBus("bus-1", updatedBus);

        // Then
        assertThat(result).isNotNull();
        verify(busRepository, times(1)).findById("bus-1");
        verify(busRepository, times(1)).save(any(Bus.class));
    }

    @Test
    void testUpdateBusNotFound() {
        // Given
        when(busRepository.findById(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> busService.updateBus("non-existent", testBus))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Bus non trouvé");
    }

    @Test
    void testDeleteBus() {
        // When
        busService.deleteBus("bus-1");

        // Then
        verify(busRepository, times(1)).deleteById("bus-1");
    }
}