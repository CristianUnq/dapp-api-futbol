package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHash(String keyHash);

    java.util.List<com.dapp.api_futbol.model.ApiKey> findByUserId(Long userId);

    java.util.Optional<com.dapp.api_futbol.model.ApiKey> findByIdAndUserId(Long id, Long userId);
    /**
     * Find an ApiKey by its fingerprint (SHA-256 Base64URL). This allows a quick
     * indexed lookup when authenticating incoming raw keys.
     */
    java.util.Optional<com.dapp.api_futbol.model.ApiKey> findByKeyFingerprint(String fingerprint);
}
