package com.geolocation_service.geolocation_service.repository;

import com.geolocation_service.geolocation_service.model.Bus;
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
class BusRepositoryTest {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private LigneBusRepository ligneBusRepository;

    private Bus testBus;
    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        busRepository.deleteAll();
        ligneBusRepository.deleteAll();

        // Créer une ligne de test
        testLigne = new LigneBus();
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setCouleur("#FF5733");
        testLigne.setActif(true);
        testLigne = ligneBusRepository.save(testLigne);

        // Créer un bus de test
        testBus = new Bus();
        testBus.setImmatriculation("A-12345-B");
        testBus.setModele("Mercedes Citaro");
        testBus.setMarque("Mercedes");
        testBus.setCapacite(80);
        testBus.setAnnee(2020);
        testBus.setStatut("EN_SERVICE");
        testBus.setLigneActuelle(testLigne);
        testBus = busRepository.save(testBus);
    }

    @Test
    void testFindByImmatriculation() {
        // When
        Bus found = busRepository.findByImmatriculation("A-12345-B");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getImmatriculation()).isEqualTo("A-12345-B");
        assertThat(found.getModele()).isEqualTo("Mercedes Citaro");
    }

    @Test
    void testFindByLigneActuelleIdLigne() {
        // When
        List<Bus> buses = busRepository.findByLigneActuelleIdLigne(testLigne.getIdLigne());

        // Then
        assertThat(buses).hasSize(1);
        assertThat(buses.get(0).getImmatriculation()).isEqualTo("A-12345-B");
    }

    @Test
    void testSaveBus() {
        // Given
        Bus newBus = new Bus();
        newBus.setImmatriculation("C-67890-D");
        newBus.setModele("Iveco Urbanway");
        newBus.setMarque("Iveco");
        newBus.setCapacite(75);
        newBus.setAnnee(2021);
        newBus.setStatut("EN_SERVICE");

        // When
        Bus saved = busRepository.save(newBus);

        // Then
        assertThat(saved.getIdBus()).isNotNull();
        assertThat(busRepository.findById(saved.getIdBus())).isPresent();
    }

    @Test
    void testDeleteBus() {
        // When
        busRepository.deleteById(testBus.getIdBus());

        // Then
        assertThat(busRepository.findById(testBus.getIdBus())).isEmpty();
    }
}