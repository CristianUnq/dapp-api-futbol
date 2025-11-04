package com.dapp.api_futbol;

import com.dapp.api_futbol.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class ApiFutbolApplicationTests {

    @Test
    void contextLoads() {
    }

}
