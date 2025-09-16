package com.dapp.api_futbol.service;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.repository.ApiKeyRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
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

        ApiKey k = new ApiKey(name, hash, user);
        apiKeyRepository.save(k);
        return raw;
    }

    public Optional<ApiKey> findByRawKey(String raw) {
        // Compare raw against stored hashes (inefficient for large sets, but okay for small projects)
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
}
