package com.geolocation_service.geolocation_service.service;

import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.repository.LigneBusRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LigneBusServiceTest {

    @Mock
    private LigneBusRepository ligneBusRepository;

    @InjectMocks
    private LigneBusService ligneBusService;

    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        testLigne = new LigneBus();
        testLigne.setIdLigne("ligne-1");
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setDescription("Ligne principale");
        testLigne.setCouleur("#FF5733");
        testLigne.setActif(true);
    }

    @Test
    void testGetAllLignes() {
        // Given
        List<LigneBus> lignes = Arrays.asList(testLigne);
        when(ligneBusRepository.findAll()).thenReturn(lignes);

        // When
        List<LigneBus> result = ligneBusService.getAllLignes();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumeroLigne()).isEqualTo("15");
        verify(ligneBusRepository, times(1)).findAll();
    }

    @Test
    void testGetLignesActives() {
        // Given
        List<LigneBus> lignes = Arrays.asList(testLigne);
        when(ligneBusRepository.findByActif(true)).thenReturn(lignes);

        // When
        List<LigneBus> result = ligneBusService.getLignesActives();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isActif()).isTrue();
        verify(ligneBusRepository, times(1)).findByActif(true);
    }

    @Test
    void testGetLigneById() {
        // Given
        when(ligneBusRepository.findById("ligne-1")).thenReturn(Optional.of(testLigne));

        // When
        Optional<LigneBus> result = ligneBusService.getLigneById("ligne-1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getNumeroLigne()).isEqualTo("15");
    }

    @Test
    void testGetLigneByNumero() {
        // Given
        when(ligneBusRepository.findByNumeroLigne("15")).thenReturn(Optional.of(testLigne));

        // When
        Optional<LigneBus> result = ligneBusService.getLigneByNumero("15");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getNomLigne()).isEqualTo("Casa - Ain Diab");
    }

    @Test
    void testCreateLigne() {
        // Given
        when(ligneBusRepository.save(any(LigneBus.class))).thenReturn(testLigne);

        // When
        LigneBus result = ligneBusService.createLigne(testLigne);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNumeroLigne()).isEqualTo("15");
        verify(ligneBusRepository, times(1)).save(testLigne);
    }

    @Test
    void testUpdateLigne() {
        // Given
        LigneBus updatedLigne = new LigneBus();
        updatedLigne.setNumeroLigne("15-A");
        updatedLigne.setNomLigne("Casa - Ain Diab Express");
        updatedLigne.setCouleur("#00FF00");
        updatedLigne.setActif(false);

        when(ligneBusRepository.findById("ligne-1")).thenReturn(Optional.of(testLigne));
        when(ligneBusRepository.save(any(LigneBus.class))).thenReturn(testLigne);

        // When
        LigneBus result = ligneBusService.updateLigne("ligne-1", updatedLigne);

        // Then
        assertThat(result).isNotNull();
        verify(ligneBusRepository, times(1)).findById("ligne-1");
        verify(ligneBusRepository, times(1)).save(any(LigneBus.class));
    }

    @Test
    void testUpdateLigneNotFound() {
        // Given
        when(ligneBusRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ligneBusService.updateLigne("non-existent", testLigne))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ligne non trouvée");
    }

    @Test
    void testDeleteLigne() {
        // When
        ligneBusService.deleteLigne("ligne-1");

        // Then
        verify(ligneBusRepository, times(1)).deleteById("ligne-1");
    }
}