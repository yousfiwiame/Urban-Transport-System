package com.geolocation_service.geolocation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.service.LigneBusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LigneBusController.class)
class LigneBusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LigneBusService ligneBusService;

    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        testLigne = new LigneBus();
        testLigne.setIdLigne("ligne-1");
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");
        testLigne.setCouleur("#FF5733");
        testLigne.setActif(true);
    }

    @Test
    void testGetAllLignes() throws Exception {
        when(ligneBusService.getAllLignes()).thenReturn(Arrays.asList(testLigne));

        mockMvc.perform(get("/api/lignes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroLigne").value("15"))
                .andExpect(jsonPath("$[0].nomLigne").value("Casa - Ain Diab"));
    }

    @Test
    void testGetLignesActives() throws Exception {
        when(ligneBusService.getLignesActives()).thenReturn(Arrays.asList(testLigne));

        mockMvc.perform(get("/api/lignes/actives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroLigne").value("15"));
    }

    @Test
    void testGetLigneById() throws Exception {
        when(ligneBusService.getLigneById("ligne-1")).thenReturn(Optional.of(testLigne));

        mockMvc.perform(get("/api/lignes/ligne-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLigne").value("15"));
    }

    @Test
    void testGetLigneByNumero() throws Exception {
        when(ligneBusService.getLigneByNumero("15")).thenReturn(Optional.of(testLigne));

        mockMvc.perform(get("/api/lignes/numero/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLigne").value("15"));
    }

    @Test
    void testCreateLigne() throws Exception {
        when(ligneBusService.createLigne(any(LigneBus.class))).thenReturn(testLigne);

        mockMvc.perform(post("/api/lignes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLigne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLigne").value("15"));
    }

    @Test
    void testUpdateLigne() throws Exception {
        when(ligneBusService.updateLigne(anyString(), any(LigneBus.class)))
                .thenReturn(testLigne);

        mockMvc.perform(put("/api/lignes/ligne-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLigne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroLigne").value("15"));
    }

    @Test
    void testDeleteLigne() throws Exception {
        mockMvc.perform(delete("/api/lignes/ligne-1"))
                .andExpect(status().isOk());
    }
}