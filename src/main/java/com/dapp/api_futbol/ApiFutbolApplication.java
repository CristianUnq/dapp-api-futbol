package com.dapp.api_futbol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiFutbolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiFutbolApplication.class, args);
	}

}
