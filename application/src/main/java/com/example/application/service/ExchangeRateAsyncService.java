package com.example.application.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface ExchangeRateAsyncService {
    @Async
    CompletableFuture<Void> fetchAndSaveRates(String base);
}
