package com.example.infra.external.client;


import org.springframework.context.annotation.Bean;

public class OandaFeignConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // logs request + response
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Example: add headers like API key, trace ID, etc.
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("X-Request-Source", "currency-service");
        };
    }
}