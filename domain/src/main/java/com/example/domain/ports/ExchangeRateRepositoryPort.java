package com.example.domain.ports;

import com.example.domain.model.ExchangeRate;

import java.util.List;

public interface ExchangeRateRepositoryPort {
    void save(ExchangeRate exchangeRate);
    List<ExchangeRate> findByBaseCurrency(String baseCurrency);
}
