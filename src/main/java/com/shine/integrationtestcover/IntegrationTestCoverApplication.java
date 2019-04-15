package com.shine.integrationtestcover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IntegrationTestCoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationTestCoverApplication.class, args);
	}

}
