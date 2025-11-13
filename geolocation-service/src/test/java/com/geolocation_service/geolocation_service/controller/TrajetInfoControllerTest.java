package com.geolocation_service.geolocation_service.controller;

import com.geolocation_service.geolocation_service.dto.TrajetInfoDTO;
import com.geolocation_service.geolocation_service.dto.LigneBusDTO;
import com.geolocation_service.geolocation_service.dto.DirectionDTO;
import com.geolocation_service.geolocation_service.service.TrajetInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrajetInfoController.class)
class TrajetInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrajetInfoService trajetInfoService;

    private TrajetInfoDTO testTrajetInfo;

    @BeforeEach
    void setUp() {
        LigneBusDTO ligneDTO = new LigneBusDTO();
        ligneDTO.setIdLigne("ligne-1");
        ligneDTO.setNumeroLigne("15");
        ligneDTO.setNomLigne("Casa - Ain Diab");
        ligneDTO.setCouleur("#FF5733");

        DirectionDTO directionDTO = new DirectionDTO();
        directionDTO.setIdDirection("dir-1");
        directionDTO.setNomDirection("Aller");
        directionDTO.setPointDepart("Casa Port");
        directionDTO.setPointArrivee("Ain Diab");

        testTrajetInfo = new TrajetInfoDTO();
        testTrajetInfo.setIdBus("bus-1");
        testTrajetInfo.setImmatriculation("A-12345-B");
        testTrajetInfo.setLigne(ligneDTO);
        testTrajetInfo.setDirection(directionDTO);
        testTrajetInfo.setLatitudeActuelle(33.5731);
        testTrajetInfo.setLongitudeActuelle(-7.5898);
        testTrajetInfo.setVitesseActuelle(45.0);
        testTrajetInfo.setDerniereMiseAJour(LocalDateTime.now());
        testTrajetInfo.setHeureDepart(LocalDateTime.now().minusMinutes(30));
        testTrajetInfo.setDistanceParcourue(12.5);
        testTrajetInfo.setDureeTrajetMinutes(30);
        testTrajetInfo.setNombreArretsEffectues(5);
        testTrajetInfo.setProchainArret("Ain Diab");
        testTrajetInfo.setDistanceProchainArret(2.3);
        testTrajetInfo.setTempsEstimeProchainArret(5);
    }

    @Test
    void testGetTrajetInfo() throws Exception {
        when(trajetInfoService.getTrajetInfo(anyString())).thenReturn(testTrajetInfo);

        mockMvc.perform(get("/api/trajet/bus/bus-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBus").value("bus-1"))
                .andExpect(jsonPath("$.immatriculation").value("A-12345-B"))
                .andExpect(jsonPath("$.ligne.numeroLigne").value("15"))
                .andExpect(jsonPath("$.direction.nomDirection").value("Aller"))
                .andExpect(jsonPath("$.latitudeActuelle").value(33.5731))
                .andExpect(jsonPath("$.vitesseActuelle").value(45.0))
                .andExpect(jsonPath("$.distanceParcourue").value(12.5))
                .andExpect(jsonPath("$.nombreArretsEffectues").value(5));
    }

    @Test
    void testGetTrajetInfoNotFound() throws Exception {
        // Le service lance maintenant une ResponseStatusException pour 404
        when(trajetInfoService.getTrajetInfo(anyString()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bus non trouvé"));

        mockMvc.perform(get("/api/trajet/bus/non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("Bus non trouvé"));
    }
}
