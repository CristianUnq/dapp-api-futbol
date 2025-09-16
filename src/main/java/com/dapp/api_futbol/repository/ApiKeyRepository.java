package com.dapp.api_futbol.repository;

import com.dapp.api_futbol.model.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHash(String keyHash);
}
