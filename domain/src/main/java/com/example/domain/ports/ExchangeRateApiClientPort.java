package com.example.domain.ports;

import com.example.domain.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateApiClientPort {
    List<ExchangeRate> fetchRates(String baseCurrency, List<String> targetCurrencies);
}