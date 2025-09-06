package com.example.application.service;

import com.example.application.dto.ExchangeRateDto;

import java.util.List;

public interface ExchangeRateApplicationService {
    void syncExchangeRates(String baseCurrency, List<String> targetCurrencies);
    List<ExchangeRateDto> getRatesByBase(String baseCurrency);
}
