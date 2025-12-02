package com.transport.urbain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.urbain.dto.request.DriverRegisterRequest;
import com.transport.urbain.dto.request.LoginRequest;
import com.transport.urbain.dto.request.RegisterRequest;
import com.transport.urbain.dto.request.UpdateProfileRequest;
import com.transport.urbain.dto.response.AuthResponse;
import com.transport.urbain.model.AuthProvider;
import com.transport.urbain.model.Role;
import com.transport.urbain.model.RoleName;
import com.transport.urbain.model.User;
import com.transport.urbain.model.UserProfile;
import com.transport.urbain.model.UserStatus;
import com.transport.urbain.repository.RefreshTokenRepository;
import com.transport.urbain.repository.RoleRepository;
import com.transport.urbain.repository.UserRepository;
import com.transport.urbain.UserServiceApplication;
import com.transport.urbain.security.SecurityConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for User Service.
 * Tests the complete flow: Registration -> Login -> Profile Management
 * Uses Testcontainers for PostgreSQL to simulate real database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserServiceIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");

        // Disable Eureka and Config Server for tests
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("spring.cloud.compatibility-verifier.enabled", () -> "false");

        // Disable Kafka for tests
        registry.add("spring.kafka.enabled", () -> "false");

        // Disable Redis for tests
        registry.add("spring.data.redis.repositories.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private RestTemplate restTemplate;

    private static String accessToken;
    private static Long userId;
    private static boolean initialized = false;

    @BeforeEach
    void setUp() {
        // Initialize roles and clean users only once before first test
        if (!initialized) {
            // Delete refresh tokens first to avoid foreign key constraint violations
            refreshTokenRepository.deleteAll();
            userRepository.deleteAll();
            
            // Initialize roles if they don't exist (roles should persist across tests)
            if (roleRepository.findByName(RoleName.PASSENGER).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleName.PASSENGER).build());
            }
            if (roleRepository.findByName(RoleName.DRIVER).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleName.DRIVER).build());
            }
            if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
                roleRepository.save(Role.builder().name(RoleName.ADMIN).build());
            }
            
            initialized = true;
        }
    }

    @Test
    @Order(1)
    @DisplayName("Should register a new passenger user successfully")
    void testRegisterPassenger() throws Exception {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .email("passenger@test.com")
                .password("Test@1234")
                .firstName("Jean")
                .lastName("Dupont")
                .phoneNumber("+212600000001")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("passenger@test.com"))
                .andExpect(jsonPath("$.user.firstName").value("Jean"))
                .andExpect(jsonPath("$.user.lastName").value("Dupont"))
                .andExpect(jsonPath("$.user.roles[0]").value("PASSENGER"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        // Verify database
        User user = userRepository.findByEmail("passenger@test.com").orElseThrow();
        assertThat(user.getEmail()).isEqualTo("passenger@test.com");
        assertThat(user.getRoles()).anyMatch(role -> role.getName() == RoleName.PASSENGER);
        assertThat(passwordEncoder.matches("Test@1234", user.getPassword())).isTrue();

        // Store userId for later tests
        userId = user.getId();
    }

    @Test
    @Order(2)
    @DisplayName("Should register a new driver user successfully")
    void testRegisterDriver() throws Exception {
        // Given
        DriverRegisterRequest request = DriverRegisterRequest.builder()
                .email("driver@test.com")
                .password("Driver@1234")
                .firstName("Mohammed")
                .lastName("Alami")
                .phoneNumber("+212600000002")
                .licenseNumber("ABC123456")
                .licenseExpirationDate("2026-12-31")
                .additionalInfo("5 years of driving experience")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("driver@test.com"))
                .andExpect(jsonPath("$.user.roles[0]").value("DRIVER"))
                .andExpect(jsonPath("$.accessToken").exists());

        // Verify database
        User user = userRepository.findByEmail("driver@test.com").orElseThrow();
        assertThat(user.getRoles()).anyMatch(role -> role.getName() == RoleName.DRIVER);
    }

    @Test
    @Order(3)
    @DisplayName("Should fail to register with existing email")
    void testRegisterDuplicateEmail() throws Exception {
        // Given - user already exists from previous test
        RegisterRequest request = RegisterRequest.builder()
                .email("passenger@test.com")
                .password("Test@1234")
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("+212600000003")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // 409 CONFLICT
    }

    @Test
    @Order(4)
    @DisplayName("Should login successfully with valid credentials")
    void testLoginSuccess() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("passenger@test.com")
                .password("Test@1234")
                .build();

        // When & Then
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("passenger@test.com"))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        // Store token for authenticated tests
        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        accessToken = authResponse.getAccessToken();

        assertThat(accessToken).isNotNull();
    }

    @Test
    @Order(5)
    @DisplayName("Should fail to login with wrong password")
    void testLoginWrongPassword() throws Exception {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("passenger@test.com")
                .password("WrongPassword@1234")
                .build();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    @DisplayName("Should get user profile with valid token")
    void testGetProfile() throws Exception {
        // Given - token from login test
        assertThat(accessToken).isNotNull();
        assertThat(userId).isNotNull();

        // When & Then - Use the correct endpoint with userId
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("passenger@test.com"))
                .andExpect(jsonPath("$.firstName").value("Jean"))
                .andExpect(jsonPath("$.lastName").value("Dupont"));
    }

    @Test
    @Order(7)
    @DisplayName("Should update user profile successfully")
    void testUpdateProfile() throws Exception {
        // Given
        assertThat(accessToken).isNotNull();
        assertThat(userId).isNotNull();
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .firstName("Jean-Claude")
                .lastName("Dupont-Martin")
                .phoneNumber("+212600000099")
                .build();

        // When & Then - Use the correct endpoint with userId
        mockMvc.perform(put("/api/users/" + userId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jean-Claude"))
                .andExpect(jsonPath("$.lastName").value("Dupont-Martin"))
                .andExpect(jsonPath("$.phoneNumber").value("+212600000099"));

        // Verify in database
        User user = userRepository.findByEmail("passenger@test.com").orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Jean-Claude");
        assertThat(user.getLastName()).isEqualTo("Dupont-Martin");
    }

    @Test
    @Order(8)
    @DisplayName("Should fail to access protected endpoint without token")
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        // When & Then
        assertThat(userId).isNotNull();
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    @DisplayName("Should get user by ID as admin")
    void testGetUserById() throws Exception {
        // Given - Create admin user first
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        User admin = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("Admin@1234"))
                .firstName("Admin")
                .lastName("User")
                .phoneNumber("+212600000010")
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .emailVerified(true)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .authProvider(AuthProvider.LOCAL)
                .build();
        
        // Create default profile for admin
        UserProfile adminProfile = UserProfile.builder()
                .user(admin)
                .notificationsEnabled(true)
                .emailNotificationsEnabled(true)
                .smsNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();
        admin.setProfile(adminProfile);
        
        userRepository.save(admin);

        // Login as admin
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@test.com")
                .password("Admin@1234")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        ).getAccessToken();

        // When & Then - Get passenger user by ID
        assertThat(userId).isNotNull();
        mockMvc.perform(get("/api/users/" + userId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("passenger@test.com"));
    }

    @Test
    @Order(10)
    @DisplayName("Should get all users with pagination")
    void testGetAllUsers() throws Exception {
        // Given - admin token from previous test
        LoginRequest loginRequest = LoginRequest.builder()
                .email("admin@test.com")
                .password("Admin@1234")
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = objectMapper.readValue(
                loginResult.getResponse().getContentAsString(),
                AuthResponse.class
        ).getAccessToken();

        // When & Then
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(3)); // passenger, driver, admin
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }
}
