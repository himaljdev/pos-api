package com.returns.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableDiscoveryClient
public class ReturnsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReturnsServiceApplication.class, args);
	}

}
