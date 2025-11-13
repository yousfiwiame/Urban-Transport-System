package com.geolocation_service.geolocation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusController.class)
class BusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BusService busService;

    private Bus testBus;

    @BeforeEach
    void setUp() {
        testBus = new Bus();
        testBus.setIdBus("bus-1");
        testBus.setImmatriculation("A-12345-B");
        testBus.setModele("Mercedes Citaro");
        testBus.setMarque("Mercedes");
        testBus.setCapacite(80);
        testBus.setAnnee(2020);
        testBus.setStatut("EN_SERVICE");
    }

    @Test
    void testGetAllBuses() throws Exception {
        // Given
        when(busService.getAllBuses()).thenReturn(Arrays.asList(testBus));

        // When & Then
        mockMvc.perform(get("/api/bus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].immatriculation").value("A-12345-B"))
                .andExpect(jsonPath("$[0].modele").value("Mercedes Citaro"));
    }

    @Test
    void testGetBusById() throws Exception {
        // Given
        when(busService.getBusById("bus-1")).thenReturn(Optional.of(testBus));

        // When & Then
        mockMvc.perform(get("/api/bus/bus-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.immatriculation").value("A-12345-B"));
    }

    @Test
    void testCreateBus() throws Exception {
        // Given
        when(busService.createBus(any(Bus.class))).thenReturn(testBus);

        // When & Then
        mockMvc.perform(post("/api/bus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.immatriculation").value("A-12345-B"));
    }

    @Test
    void testUpdateBus() throws Exception {
        // Given
        when(busService.updateBus(anyString(), any(Bus.class))).thenReturn(testBus);

        // When & Then
        mockMvc.perform(put("/api/bus/bus-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.immatriculation").value("A-12345-B"));
    }

    @Test
    void testDeleteBus() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/bus/bus-1"))
                .andExpect(status().isOk());
    }
}