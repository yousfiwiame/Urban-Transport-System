package com.geolocation_service.geolocation_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolocation_service.geolocation_service.model.Bus;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.service.PositionBusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PositionBusController.class)
class PositionBusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PositionBusService positionBusService;

    private PositionBus testPosition;
    private Bus testBus;

    @BeforeEach
    void setUp() {
        testBus = new Bus();
        testBus.setIdBus("bus-1");
        testBus.setImmatriculation("A-12345-B");

        testPosition = new PositionBus();
        testPosition.setIdPosition("pos-1");
        testPosition.setLatitude(33.5731);
        testPosition.setLongitude(-7.5898);
        testPosition.setVitesse(45.0);
        testPosition.setTimestamp(LocalDateTime.now());
        testPosition.setBus(testBus);
    }

    @Test
    void testGetAllPositions() throws Exception {
        when(positionBusService.getAllPositions()).thenReturn(Arrays.asList(testPosition));

        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].latitude").value(33.5731))
                .andExpect(jsonPath("$[0].vitesse").value(45.0));
    }

    @Test
    void testGetPositionsByBus() throws Exception {
        when(positionBusService.getPositionsByBusId("bus-1"))
                .thenReturn(Arrays.asList(testPosition));

        mockMvc.perform(get("/api/positions/bus/bus-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].latitude").value(33.5731));
    }

    @Test
    void testAddPosition() throws Exception {
        when(positionBusService.addPosition(any(PositionBus.class)))
                .thenReturn(testPosition);

        mockMvc.perform(post("/api/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPosition)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latitude").value(33.5731));
    }
}