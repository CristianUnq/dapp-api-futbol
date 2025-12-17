package com.dapp.api_futbol.transaction;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.repository.ApiKeyRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionalTestHelper {

    private final ApiKeyRepository apiKeyRepository;

    public TransactionalTestHelper(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Transactional
    public void saveAndThrow(ApiKey apiKey) {
        apiKeyRepository.save(apiKey);
        throw new RuntimeException("force rollback");
    }
}
