package com.geolocation_service.geolocation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolocation_service.geolocation_service.model.Direction;
import com.geolocation_service.geolocation_service.model.LigneBus;
import com.geolocation_service.geolocation_service.service.DirectionService;
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

@WebMvcTest(DirectionController.class)
class DirectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DirectionService directionService;

    private Direction testDirection;
    private LigneBus testLigne;

    @BeforeEach
    void setUp() {
        testLigne = new LigneBus();
        testLigne.setIdLigne("ligne-1");
        testLigne.setNumeroLigne("15");
        testLigne.setNomLigne("Casa - Ain Diab");

        testDirection = new Direction();
        testDirection.setIdDirection("dir-1");
        testDirection.setNomDirection("Aller");
        testDirection.setPointDepart("Casa Port");
        testDirection.setPointArrivee("Ain Diab");
        testDirection.setLigne(testLigne);
    }

    @Test
    void testGetAllDirections() throws Exception {
        when(directionService.getAllDirections()).thenReturn(Arrays.asList(testDirection));

        mockMvc.perform(get("/api/directions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomDirection").value("Aller"))
                .andExpect(jsonPath("$[0].pointDepart").value("Casa Port"));
    }

    @Test
    void testGetDirectionsByLigne() throws Exception {
        when(directionService.getDirectionsByLigne("ligne-1"))
                .thenReturn(Arrays.asList(testDirection));

        mockMvc.perform(get("/api/directions/ligne/ligne-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomDirection").value("Aller"));
    }

    @Test
    void testGetDirectionById() throws Exception {
        when(directionService.getDirectionById("dir-1"))
                .thenReturn(Optional.of(testDirection));

        mockMvc.perform(get("/api/directions/dir-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDirection").value("Aller"));
    }

    @Test
    void testCreateDirection() throws Exception {
        when(directionService.createDirection(any(Direction.class)))
                .thenReturn(testDirection);

        mockMvc.perform(post("/api/directions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDirection)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDirection").value("Aller"));
    }

    @Test
    void testUpdateDirection() throws Exception {
        when(directionService.updateDirection(anyString(), any(Direction.class)))
                .thenReturn(testDirection);

        mockMvc.perform(put("/api/directions/dir-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDirection)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDirection").value("Aller"));
    }

    @Test
    void testDeleteDirection() throws Exception {
        mockMvc.perform(delete("/api/directions/dir-1"))
                .andExpect(status().isOk());
    }
}