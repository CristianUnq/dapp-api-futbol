package com.dapp.api_futbol.transaction;

import com.dapp.api_futbol.model.ApiKey;
import com.dapp.api_futbol.model.User;
import com.dapp.api_futbol.repository.ApiKeyRepository;
import com.dapp.api_futbol.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@SpringBootTest
public class TransactionalIntegrationTest {

    @Autowired
    TransactionalTestHelper helper;

    @Autowired
    ApiKeyRepository apiKeyRepository;

    @Autowired
    UserRepository userRepository;

    long apiKeyRegistersBefore;
    long userRegistersBefore;
    long apiKeyRegistersAfter;
    long userRegistersAfter;


    @BeforeEach
    void setUp() {
        apiKeyRegistersBefore = apiKeyRepository.count();
        userRegistersBefore = userRepository.count();

        String uname = "testuser-" + UUID.randomUUID();
        User u = new User(uname, "encoded");
        u = userRepository.save(u);

        ApiKey k = new ApiKey("test", "hash", "fingerprint", u);

        assertThrows(RuntimeException.class, () -> helper.saveAndThrow(k));

        // Read counts from a separate REQUIRES_NEW transaction to observe committed state
        apiKeyRegistersAfter = apiKeyRepository.count();
        userRegistersAfter = userRepository.count();
    }

    @Test
    void transactionalMethodShouldRollbackOnRuntimeExceptionInTableApiKey() {
        assertEquals(apiKeyRegistersBefore, apiKeyRegistersAfter, "El registro que se agrego en la tabla ApiKey debe haber sido revertido por la transacción");
    }

    @Test
    void transactionalMethodShouldRollbackOnRuntimeExceptionInTableUser() {
        assertNotEquals("El registro que se agrego en la tabla User NO debe haber sido revertido por la transacción", userRegistersBefore, userRegistersAfter);
    }

}
