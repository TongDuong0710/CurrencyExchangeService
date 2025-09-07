package com.example.application.service.impl;

import com.example.application.service.ExchangeRateAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceAsyncImpl implements ExchangeRateAsyncService {

    private final ExchangeRateApplicationServiceImpl exchangeRateService;

    @Override
    @Async("exchangeRateExecutor")
    public CompletableFuture<Void> fetchAndSaveRates(String base) {
        try {
            var rates = exchangeRateService.getLiveRatesByBase(base);
            exchangeRateService.saveOrUpdateAll(rates);
            log.info("Completed async sync for base {}", base);
        } catch (Exception e) {
            log.error("Failed to sync base {}", base, e);
        }
        return CompletableFuture.completedFuture(null);
    }
}
