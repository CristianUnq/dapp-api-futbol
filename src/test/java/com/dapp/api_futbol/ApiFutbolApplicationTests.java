package com.dapp.api_futbol;

import com.dapp.api_futbol.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {TestSecurityConfig.class})
@ActiveProfiles("test")
class ApiFutbolApplicationTests {

    @Test
    void contextLoads() {
    }

}
