package com.example.domain.ports;

import com.example.domain.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepositoryPort {

    /**
     * Save a new exchange rate or update an existing one.
     */
    ExchangeRate save(ExchangeRate exchangeRate);

    /**
     * Save multiple exchange rates at once.
     */
    List<ExchangeRate> saveAll(List<ExchangeRate> exchangeRates);

    /**
     * Find all exchange rates by base currency.
     */
    List<ExchangeRate> findByBaseCurrency(String baseCurrency);

    /**
     * Find a specific exchange rate by base and target.
     */
    Optional<ExchangeRate> findByBaseAndTarget(String baseCurrency, String targetCurrency);

    /**
     * Delete all exchange rates for a given base currency.
     */
    void deleteByBaseCurrency(String baseCurrency);
}
