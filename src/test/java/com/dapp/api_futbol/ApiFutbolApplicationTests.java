package com.dapp.api_futbol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.dapp.api_futbol.service.WhoScoreService;

@SpringBootTest
class ApiFutbolApplicationTests {

	@MockitoBean
    private WhoScoreService whoScoreService;

	@Test
	void contextLoads() {
	}

}
