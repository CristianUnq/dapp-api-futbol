package com.dapp.api_futbol.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.security.JwtTokenProvider;
import com.dapp.api_futbol.service.ApiKeyService;
import com.dapp.api_futbol.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test suite for the Authentication Controller endpoints.
 * 
 * This test class uses @WebMvcTest slice testing to focus on the web layer,
 * specifically the authentication endpoints. The SecurityConfig is explicitly
 * imported to ensure proper security context configuration.
 * 
 * Key features tested:
 * - User registration
 * - User login with JWT token generation
 * - API key management (creation, listing, revocation)
 * - Security constraints enforcement
 * 
 * The tests use MockMvc for request simulation and mock services for isolation.
 * CSRF protection and user authentication are properly configured for each test case.
 */
@WebMvcTest(AuthController.class)
@Import(com.dapp.api_futbol.config.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private ApiKeyService apiKeyService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "encoded-password");
        testUser.setId(1L);
    }

    @Test
    void register_success() throws Exception {
        // given
        when(userService.register(eq("testuser"), eq("password")))
            .thenReturn(testUser);

        // when/then
        mockMvc.perform(post("/auth/register")
                .with(csrf())  // Agrega token CSRF
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "username", "testuser",
                    "password", "password"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    void login_success() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser", 3600)).thenReturn("test-jwt-token");

        // when/then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "username", "testuser",
                    "password", "password"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", is("test-jwt-token")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.expiresIn", is(3600)));
    }

    @Test
    void login_userNotFound() throws Exception {
        // given
        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // when/then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "username", "nonexistent",
                    "password", "password"
                ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Invalid credentials")));
    }

    @Test
    void login_incorrectPassword() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "encoded-password")).thenReturn(false);

        // when/then
        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "username", "testuser",
                    "password", "wrongpass"
                ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Invalid credentials")));
    }

    @Test
    void createApiKey_success() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(apiKeyService.createForUser(eq(testUser), eq("test-key")))
            .thenReturn("raw-api-key-value");

        // when/then
        mockMvc.perform(post("/auth/apikeys")
                .with(csrf())
                .with(user("testuser"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "name", "test-key"
                ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKey", is("raw-api-key-value")));
    }

    @Test
    void createApiKey_unauthorized() throws Exception {
        // when/then - sin @WithMockUser
        mockMvc.perform(post("/auth/apikeys")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "name", "test-key"
                ))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listApiKeys_success() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        ApiKey key1 = new ApiKey("key1", "hash1", "fp1", testUser);
        ApiKey key2 = new ApiKey("key2", "hash2", "fp2", testUser);
        key1.setId(1L);
        key2.setId(2L);
        when(apiKeyService.listForUser(testUser))
            .thenReturn(Arrays.asList(key1, key2));

        // when/then
        mockMvc.perform(get("/auth/apikeys")
                .with(csrf())
                .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("key1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("key2")))
                // Verificar que no se exponen los hashes
                .andExpect(jsonPath("$[*].keyHash").doesNotExist());
    }

    @Test
    void listApiKeys_unauthorized() throws Exception {
        // when/then - sin @WithMockUser
    mockMvc.perform(get("/auth/apikeys")
        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void revokeApiKey_success() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(apiKeyService.revokeByIdForUser(1L, testUser)).thenReturn(true);

        // when/then
    mockMvc.perform(delete("/auth/apikeys/1")
        .with(csrf())
        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("revoked")));
    }

    @Test
    void revokeApiKey_notFound() throws Exception {
        // given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(apiKeyService.revokeByIdForUser(999L, testUser)).thenReturn(false);

    // when/then
    mockMvc.perform(delete("/auth/apikeys/999")
        .with(csrf())
        .with(user("testuser")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error", is("Not found")));
    }

    @Test
    void revokeApiKey_unauthorized() throws Exception {
        // when/then - sin @WithMockUser
    mockMvc.perform(delete("/auth/apikeys/1")
        .with(csrf()))
        .andExpect(status().isUnauthorized());
    }
}