package com.geolocation_service.geolocation_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geolocation_service.geolocation_service.model.PositionBus;
import com.geolocation_service.geolocation_service.repository.PositionBusRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Geolocation Service.
 * Tests: Store Bus Positions -> Retrieve Positions -> Track Bus in Real-time
 * Uses Testcontainers for MongoDB
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GeolocationServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        // Disable Eureka and Config
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");

        // Disable Redis for tests
        registry.add("spring.data.redis.repositories.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PositionBusRepository positionBusRepository;

    private static String positionId;
    private static Long busId = 101L;

    @BeforeEach
    void setUp() {
        if (positionId == null) {
            positionBusRepository.deleteAll();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should add bus position successfully")
    void testAddPosition() throws Exception {
        // Given
        PositionBus position = PositionBus.builder()
                .busId(busId)
                .latitude(33.5731)
                .longitude(-7.5898) // Casablanca coordinates
                .vitesse(45.5)
                .direction(90.0)
                .timestamp(LocalDateTime.now())
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.busId").value(busId))
                .andExpect(jsonPath("$.latitude").value(33.5731))
                .andExpect(jsonPath("$.longitude").value(-7.5898))
                .andExpect(jsonPath("$.vitesse").value(45.5))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PositionBus savedPosition = objectMapper.readValue(responseBody, PositionBus.class);
        positionId = savedPosition.getIdPosition();

        // Verify in database
        assertThat(positionBusRepository.findById(positionId)).isPresent();
    }

    @Test
    @Order(2)
    @DisplayName("Should get all positions")
    void testGetAllPositions() throws Exception {
        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].busId").value(busId))
                .andExpect(jsonPath("$[0].latitude").exists());
    }

    @Test
    @Order(3)
    @DisplayName("Should get positions for specific bus")
    void testGetPositionsByBusId() throws Exception {
        mockMvc.perform(get("/api/positions/bus/" + busId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].busId").value(busId));
    }

    @Test
    @Order(4)
    @DisplayName("Should add multiple positions for tracking")
    void testAddMultiplePositions() throws Exception {
        // Add 5 positions to simulate bus movement
        for (int i = 1; i <= 5; i++) {
            PositionBus position = PositionBus.builder()
                    .busId(busId)
                    .latitude(33.5731 + (i * 0.001)) // Simulating movement
                    .longitude(-7.5898 + (i * 0.001))
                    .vitesse(45.5 + i)
                    .direction(90.0)
                    .timestamp(LocalDateTime.now().plusSeconds(i * 10))
                    .build();

            mockMvc.perform(post("/api/positions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(position)))
                    .andExpect(status().isCreated());
        }

        // Verify we now have 6 positions total (1 from first test + 5 new)
        mockMvc.perform(get("/api/positions/bus/" + busId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
    }

    @Test
    @Order(5)
    @DisplayName("Should get latest position for a bus")
    void testGetLatestPosition() throws Exception {
        // The service should return the most recent position
        MvcResult result = mockMvc.perform(get("/api/positions/bus/" + busId))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        PositionBus[] positions = objectMapper.readValue(responseBody, PositionBus[].class);

        // Latest position should have the highest latitude (from our simulation)
        assertThat(positions).isNotEmpty();
        assertThat(positions[positions.length - 1].getLatitude()).isGreaterThan(33.5731);
    }

    @Test
    @Order(6)
    @DisplayName("Should add position for another bus")
    void testAddPositionForAnotherBus() throws Exception {
        Long anotherBusId = 102L;

        PositionBus position = PositionBus.builder()
                .busId(anotherBusId)
                .latitude(33.9716) // Rabat coordinates
                .longitude(-6.8498)
                .vitesse(60.0)
                .direction(180.0)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(position)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.busId").value(anotherBusId));

        // Verify we can get positions for the new bus
        mockMvc.perform(get("/api/positions/bus/" + anotherBusId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].busId").value(anotherBusId));
    }

    @Test
    @Order(7)
    @DisplayName("Should get all active bus positions")
    void testGetAllActiveBusPositions() throws Exception {
        // All positions from tests are recent, so all should be active
        mockMvc.perform(get("/api/positions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(7)));
    }

    @Test
    @Order(8)
    @DisplayName("Should handle invalid position data")
    void testAddInvalidPosition() throws Exception {
        // Invalid latitude (out of range)
        PositionBus invalidPosition = PositionBus.builder()
                .busId(103L)
                .latitude(200.0) // Invalid - max is 90
                .longitude(-7.5898)
                .vitesse(45.5)
                .timestamp(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/api/positions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPosition)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Should get empty list for non-existent bus")
    void testGetPositionsForNonExistentBus() throws Exception {
        Long nonExistentBusId = 99999L;

        mockMvc.perform(get("/api/positions/bus/" + nonExistentBusId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @Order(10)
    @DisplayName("Should verify MongoDB data persistence")
    void testDataPersistence() throws Exception {
        // Verify data is actually persisted in MongoDB
        long count = positionBusRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(7); // At least 7 positions created

        // Verify we can query by busId directly from repository
        var positionsForBus101 = positionBusRepository.findByBusId(101L);
        assertThat(positionsForBus101).hasSize(6);

        var positionsForBus102 = positionBusRepository.findByBusId(102L);
        assertThat(positionsForBus102).hasSize(1);
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.stop();
    }
}
