package com.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot application entry point.
 * Wires up dependencies manually for demonstration.
 */
@SpringBootApplication(scanBasePackages = {"com.example"})
@ConfigurationPropertiesScan(basePackages = {"com.example"})
@EntityScan("com.example.infra.entity")
@EnableJpaRepositories(basePackages = "com.example.infra.repository")
@EnableJpaAuditing
public class CurrencyExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyExchangeApplication.class, args);
    }
}

