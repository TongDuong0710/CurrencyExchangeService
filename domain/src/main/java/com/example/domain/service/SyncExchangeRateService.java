package com.example.domain.service;


import com.example.domain.model.ExchangeRate;
import com.example.domain.ports.ExchangeRateApiClientPort;
import com.example.domain.ports.ExchangeRateRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SyncExchangeRateService {
    private final ExchangeRateApiClientPort apiClient;
    private final ExchangeRateRepositoryPort exchangeRateRepository;

    public void syncRates(String baseCurrency, List<String> targetCurrencies) {
        List<ExchangeRate> rates = apiClient.fetchRates(baseCurrency, targetCurrencies);
        rates.forEach(exchangeRateRepository::save);
    }
}
