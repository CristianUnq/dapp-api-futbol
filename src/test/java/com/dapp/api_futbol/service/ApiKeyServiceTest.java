package com.dapp.api_futbol.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.repository.ApiKeyRepository;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    private ApiKeyService apiKeyService;
    private User testUser;

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(apiKeyRepository);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void createForUser_generatesValidKeyAndStoresHash() {
        // when
        String raw = apiKeyService.createForUser(testUser, "Test Key");

        // then
        verify(apiKeyRepository).save(argThat(apiKey -> {
            // Verify key name
            assertEquals("Test Key", apiKey.getName());
            
            // Verify fingerprint is base64url encoded SHA-256
            assertTrue(apiKey.getKeyFingerprint().matches("^[A-Za-z0-9_-]{43}$"));
            
            // Verify hash is valid bcrypt and matches raw key
            assertTrue(apiKey.getKeyHash().startsWith("$2a$"));
            assertTrue(BCrypt.checkpw(raw, apiKey.getKeyHash()));
            
            // Verify user association
            assertEquals(testUser, apiKey.getUser());
            
            return true;
        }));
    }

    @Test
    void findByRawKey_fastPath_findsKeyByFingerprintAndVerifiesHash() {
        // given
        String raw = "test-key-123";
        String hash = BCrypt.hashpw(raw, BCrypt.gensalt());
        ApiKey apiKey = new ApiKey("Test Key", hash, sha256Base64Url(raw), testUser);
        when(apiKeyRepository.findByKeyFingerprint(sha256Base64Url(raw)))
            .thenReturn(Optional.of(apiKey));

        // when
        Optional<ApiKey> found = apiKeyService.findByRawKey(raw);

        // then
        assertTrue(found.isPresent());
        assertEquals(apiKey, found.get());
        verify(apiKeyRepository, never()).findAll(); // No fallback needed
    }

    @Test
    void findByRawKey_fallback_findsKeyByHashWhenFingerprintFails() {
        // given
        String raw = "test-key-123";
        String hash = BCrypt.hashpw(raw, BCrypt.gensalt());
        ApiKey apiKey = new ApiKey("Test Key", hash, "wrong-fingerprint", testUser);
        
        when(apiKeyRepository.findByKeyFingerprint(any())).thenReturn(Optional.empty());
        when(apiKeyRepository.findAll()).thenReturn(Arrays.asList(apiKey));

        // when
        Optional<ApiKey> found = apiKeyService.findByRawKey(raw);

        // then
        assertTrue(found.isPresent());
        assertEquals(apiKey, found.get());
        verify(apiKeyRepository).findAll(); // Fallback was used
    }

    @Test
    void findByRawKey_notFound_returnsEmpty() {
        // given
        String raw = "nonexistent-key";
        when(apiKeyRepository.findByKeyFingerprint(any())).thenReturn(Optional.empty());
        when(apiKeyRepository.findAll()).thenReturn(Arrays.asList());

        // when
        Optional<ApiKey> found = apiKeyService.findByRawKey(raw);

        // then
        assertFalse(found.isPresent());
        verify(apiKeyRepository).findAll(); // Fallback was attempted
    }

    @Test
    void listForUser_returnsUserKeys() {
        // given
        ApiKey key1 = new ApiKey("Key 1", "hash1", "fp1", testUser);
        ApiKey key2 = new ApiKey("Key 2", "hash2", "fp2", testUser);
        when(apiKeyRepository.findByUserId(testUser.getId()))
            .thenReturn(Arrays.asList(key1, key2));

        // when
        var keys = apiKeyService.listForUser(testUser);

        // then
        assertEquals(2, keys.size());
        assertTrue(keys.contains(key1));
        assertTrue(keys.contains(key2));
    }

    @Test
    void revokeByIdForUser_existingKey_revokesAndReturnsTrue() {
        // given
        ApiKey key = new ApiKey("Test Key", "hash", "fp", testUser);
        when(apiKeyRepository.findByIdAndUserId(1L, testUser.getId()))
            .thenReturn(Optional.of(key));

        // when
        boolean result = apiKeyService.revokeByIdForUser(1L, testUser);

        // then
        assertTrue(result);
        verify(apiKeyRepository).delete(key);
    }

    @Test
    void revokeByIdForUser_nonexistentKey_returnsFalse() {
        // given
        when(apiKeyRepository.findByIdAndUserId(1L, testUser.getId()))
            .thenReturn(Optional.empty());

        // when
        boolean result = apiKeyService.revokeByIdForUser(1L, testUser);

        // then
        assertFalse(result);
        verify(apiKeyRepository, never()).delete(any());
    }

    // Helper method to replicate ApiKeyService's sha256Base64Url
    private String sha256Base64Url(String raw) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(d);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}