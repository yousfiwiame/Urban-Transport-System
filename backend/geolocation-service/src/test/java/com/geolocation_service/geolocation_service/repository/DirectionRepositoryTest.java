package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Direction;
import com.geolocation_service.geolocation_service.model.LigneBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DirectionRepositoryTest {

    @Autowired
    private DirectionRepository directionRepository;

    @Autowired
    private LigneBusRepository ligneBusRepository;

    private LigneBus testLigne;
    private Direction directionAller;
    private Direction directionRetour;

    @BeforeEach
    void setUp() {
        directionRepository.deleteAll();
        ligneBusRepository.deleteAll();

        // Créer une ligne de test
        testLigne = new LigneBus();
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setCouleur("#FF5733");
        testLigne.setActif(true);
        testLigne = ligneBusRepository.save(testLigne);

        // Créer direction aller
        directionAller = new Direction();
        directionAller.setNomDirection("Aller");
        directionAller.setPointDepart("Casa Port");
        directionAller.setPointArrivee("Ain Diab");
        directionAller.setLigne(testLigne);
        directionAller = directionRepository.save(directionAller);

        // Créer direction retour
        directionRetour = new Direction();
        directionRetour.setNomDirection("Retour");
        directionRetour.setPointDepart("Ain Diab");
        directionRetour.setPointArrivee("Casa Port");
        directionRetour.setLigne(testLigne);
        directionRetour = directionRepository.save(directionRetour);
    }

    @Test
    void testFindByLigneIdLigne() {
        // When
        List<Direction> directions = directionRepository.findByLigneIdLigne(testLigne.getIdLigne());

        // Then
        assertThat(directions).hasSize(2);
        assertThat(directions).extracting("nomDirection")
                .containsExactlyInAnyOrder("Aller", "Retour");
    }

    @Test
    void testSaveDirection() {
        // Given
        Direction newDirection = new Direction();
        newDirection.setNomDirection("Variante");
        newDirection.setPointDepart("Casa Port");
        newDirection.setPointArrivee("Anfa");
        newDirection.setLigne(testLigne);

        // When
        Direction saved = directionRepository.save(newDirection);

        // Then
        assertThat(saved.getIdDirection()).isNotNull();
        assertThat(directionRepository.findById(saved.getIdDirection())).isPresent();
    }

    @Test
    void testDeleteDirection() {
        // When
        directionRepository.deleteById(directionAller.getIdDirection());

        // Then
        List<Direction> remaining = directionRepository.findByLigneIdLigne(testLigne.getIdLigne());
        assertThat(remaining).hasSize(1);
        assertThat(remaining.get(0).getNomDirection()).isEqualTo("Retour");
    }
}