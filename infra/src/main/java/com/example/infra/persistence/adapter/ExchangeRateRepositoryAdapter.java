package com.example.infra.persistence.adapter;


import com.example.application.provider.ExchangeRateRepositoryProvider;
import com.example.domain.model.ExchangeRate;
import com.example.infra.persistence.entity.ExchangeRateEntity;
import com.example.infra.persistence.repository.JpaExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExchangeRateRepositoryAdapter implements ExchangeRateRepositoryProvider {

    private final JpaExchangeRateRepository jpaRepository;

    @Override
    public void save(ExchangeRate exchangeRate) {
        jpaRepository.save(toEntity(exchangeRate));
    }

    @Override
    public List<ExchangeRate> findByBaseCurrency(String baseCurrency) {
        return jpaRepository.findByBaseCurrency(baseCurrency)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ExchangeRate toDomain(ExchangeRateEntity entity) {
        return new ExchangeRate(entity.getBaseCurrency(), entity.getTargetCurrency(),
                entity.getRate(), entity.getTimestamp());
    }

    private ExchangeRateEntity toEntity(ExchangeRate rate) {
        return ExchangeRateEntity.builder()
                .baseCurrency(rate.getBaseCurrency())
                .targetCurrency(rate.getTargetCurrency())
                .rate(rate.getRate())
                .timestamp(rate.getTimestamp())
                .build();
    }
}