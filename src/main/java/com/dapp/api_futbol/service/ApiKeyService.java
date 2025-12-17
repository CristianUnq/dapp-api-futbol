package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.repository.ApiKeyRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final SecureRandom random = new SecureRandom();

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    /**
     * Generates a new API key (returning the raw key to the caller) and stores only the bcrypt hash.
     */
    public String createForUser(User user, String name) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String hash = BCrypt.hashpw(raw, BCrypt.gensalt(12));

        // fingerprint: SHA-256 Base64URL (indexable)
        // We store this fingerprint to avoid scanning the whole table when a request arrives.
        // However, fingerprint collisions are still checked by verifying the bcrypt hash.
        String fingerprint = sha256Base64Url(raw);

        ApiKey k = new ApiKey(name, hash, fingerprint, user);
        apiKeyRepository.save(k);
        return raw;
    }

    public Optional<ApiKey> findByRawKey(String raw) {
        // Compute fingerprint and attempt indexed lookup first (fast path)
        String fingerprint = sha256Base64Url(raw);
        var byFingerprint = apiKeyRepository.findByKeyFingerprint(fingerprint);
        if (byFingerprint.isPresent()) {
            ApiKey k = byFingerprint.get();
            // Verify bcrypt to confirm the raw key
            if (BCrypt.checkpw(raw, k.getKeyHash())) return Optional.of(k);
        }

        // Fallback: brute-force check. This handles older rows without fingerprint
        // or very rare hash collisions. Keep as a safety net but avoid relying on it.
        for (ApiKey k : apiKeyRepository.findAll()) {
            if (BCrypt.checkpw(raw, k.getKeyHash())) {
                return Optional.of(k);
            }
        }
        return Optional.empty();
    }

    public void revoke(ApiKey apiKey) {
        apiKeyRepository.delete(apiKey);
    }

    public java.util.List<ApiKey> listForUser(User user) {
        return apiKeyRepository.findByUserId(user.getId());
    }

    public boolean revokeByIdForUser(Long id, User user) {

 
        var opt = apiKeyRepository.findByIdAndUserId(id, user.getId());
        if (opt.isPresent()) {
            apiKeyRepository.delete(opt.get());
            return true;
        }
        return false;
    }

    private String sha256Base64Url(String raw) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(d);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
