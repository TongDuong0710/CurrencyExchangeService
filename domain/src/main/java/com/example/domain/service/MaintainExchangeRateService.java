package com.example.domain.service;

import com.example.domain.model.ExchangeRate;
import com.example.domain.ports.ExchangeRateRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintainExchangeRateService {

    private final ExchangeRateRepositoryPort exchangeRateRepository;

    /**
     * Save or update a single exchange rate.
     */
    public ExchangeRate save(ExchangeRate exchangeRate) {
        return exchangeRateRepository.save(exchangeRate);
    }

    /**
     * Save or update multiple exchange rates in batch.
     */
    public List<ExchangeRate> saveAll(List<ExchangeRate> exchangeRates) {
        return exchangeRateRepository.saveAll(exchangeRates);
    }

    /**
     * Find all exchange rates by base currency.
     */
    public List<ExchangeRate> findByBaseCurrency(String baseCurrency) {
        return exchangeRateRepository.findByBaseCurrency(baseCurrency);
    }

    /**
     * Find a specific exchange rate by base and target currency.
     */
    public ExchangeRate findByBaseAndTarget(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findByBaseAndTarget(baseCurrency, targetCurrency)
                .orElseThrow(() ->
                        new IllegalArgumentException("Rate not found for " + baseCurrency + "→" + targetCurrency));
    }

    /**
     * Delete all exchange rates for a given base currency.
     */
    public void deleteByBaseCurrency(String baseCurrency) {
        exchangeRateRepository.deleteByBaseCurrency(baseCurrency);
    }
}
