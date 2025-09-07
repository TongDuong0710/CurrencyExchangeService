package com.example.application.provider;

import com.example.domain.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateApiClientProvider {
    ExchangeRate fetchRates(String baseCurrency, String targetCurrency);
    List<ExchangeRate> fetchRates(String baseCurrency);
}
