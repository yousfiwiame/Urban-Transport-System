package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.LigneBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(locations = "classpath:application-test.properties")
class LigneBusRepositoryTest {

    @Autowired
    private LigneBusRepository ligneBusRepository;

    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        ligneBusRepository.deleteAll();

        testLigne = new LigneBus();
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setDescription("Ligne principale reliant Casa Port à Ain Diab");
        testLigne.setCouleur("#FF5733");
        testLigne.setActif(true);
        testLigne = ligneBusRepository.save(testLigne);
    }

    @Test
    void testFindByNumeroLigne() {
        // When
        Optional<LigneBus> found = ligneBusRepository.findByNumeroLigne("15");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNomLigne()).isEqualTo("Casa - Ain Diab");
        assertThat(found.get().getCouleur()).isEqualTo("#FF5733");
    }

    @Test
    void testFindByActif() {
        // Given - Créer une ligne inactive
        LigneBus inactiveLigne = new LigneBus();
        inactiveLigne.setNumeroLigne("20");
        inactiveLigne.setNomLigne("Ligne Désactivée");
        inactiveLigne.setCouleur("#999999");
        inactiveLigne.setActif(false);
        ligneBusRepository.save(inactiveLigne);

        // When
        List<LigneBus> activeLignes = ligneBusRepository.findByActif(true);
        List<LigneBus> inactiveLignes = ligneBusRepository.findByActif(false);

        // Then
        assertThat(activeLignes).hasSize(1);
        assertThat(activeLignes.get(0).getNumeroLigne()).isEqualTo("15");
        assertThat(inactiveLignes).hasSize(1);
        assertThat(inactiveLignes.get(0).getNumeroLigne()).isEqualTo("20");
    }

    @Test
    void testSaveLigne() {
        // Given
        LigneBus newLigne = new LigneBus();
        newLigne.setNumeroLigne("25");
        newLigne.setNomLigne("Hay Hassani - Sidi Moumen");
        newLigne.setCouleur("#3498DB");
        newLigne.setActif(true);

        // When
        LigneBus saved = ligneBusRepository.save(newLigne);

        // Then
        assertThat(saved.getIdLigne()).isNotNull();
        assertThat(ligneBusRepository.findById(saved.getIdLigne())).isPresent();
    }

    @Test
    void testUpdateLigne() {
        // Given
        testLigne.setCouleur("#00FF00");
        testLigne.setActif(false);

        // When
        LigneBus updated = ligneBusRepository.save(testLigne);

        // Then
        assertThat(updated.getCouleur()).isEqualTo("#00FF00");
        assertThat(updated.isActif()).isFalse();
    }

    @Test
    void testDeleteLigne() {
        // When
        ligneBusRepository.deleteById(testLigne.getIdLigne());

        // Then
        assertThat(ligneBusRepository.findById(testLigne.getIdLigne())).isEmpty();
    }
}