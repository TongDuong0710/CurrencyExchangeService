package com.example.domain.ports;

import com.example.domain.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepositoryPort {
    List<Currency> findAll();
    Optional<Currency> findByCode(String code);
    Currency save(Currency currency);
    void deleteByCode(String code);
}
