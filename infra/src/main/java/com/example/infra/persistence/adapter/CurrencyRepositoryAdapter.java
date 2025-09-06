package com.example.infra.persistence.adapter;


import com.example.application.provider.CurrencyRepositoryProvider;
import com.example.domain.model.Currency;
import com.example.infra.persistence.entity.CurrencyEntity;
import com.example.infra.persistence.repository.JpaCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurrencyRepositoryAdapter implements CurrencyRepositoryProvider {

    private final JpaCurrencyRepository jpaRepository;

    @Override
    public List<Currency> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return jpaRepository.findById(code).map(this::toDomain);
    }

    @Override
    public Currency save(Currency currency) {
        CurrencyEntity saved = jpaRepository.save(toEntity(currency));
        return toDomain(saved);
    }

    @Override
    public void deleteByCode(String code) {
        jpaRepository.deleteById(code);
    }

    private Currency toDomain(CurrencyEntity entity) {
        return new Currency(entity.getCode(), entity.getName(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private CurrencyEntity toEntity(Currency currency) {
        return CurrencyEntity.builder()
                .code(currency.getCode())
                .name(currency.getName())
                .createdAt(currency.getCreatedAt())
                .updatedAt(currency.getUpdatedAt())
                .build();
    }
}