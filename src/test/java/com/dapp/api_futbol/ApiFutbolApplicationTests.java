package com.dapp.api_futbol;

import com.dapp.api_futbol.config.TestSecurityConfig;
import com.dapp.api_futbol.service.ApiKeyService;
import com.dapp.api_futbol.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ApiFutbolApplicationTests {

    @MockBean
    private ApiKeyService apiKeyService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void contextLoads() {
    }
}