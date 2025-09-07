package com.example.infra.persistence.adapter;

import com.example.application.provider.ExchangeRateRepositoryProvider;
import com.example.domain.model.ExchangeRate;
import com.example.infra.persistence.entity.ExchangeRateEntity;
import com.example.infra.persistence.mapper.ExchangeRateEntityMapper;
import com.example.infra.persistence.repository.JpaExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExchangeRateRepositoryAdapter implements ExchangeRateRepositoryProvider {

    private final JpaExchangeRateRepository jpaRepository;
    private final ExchangeRateEntityMapper mapper;

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        // Check if exists by base + target
        Optional<ExchangeRateEntity> existingOpt =
                jpaRepository.findByBaseCurrencyAndTargetCurrency(
                        exchangeRate.getBaseCurrency(),
                        exchangeRate.getTargetCurrency()
                );

        ExchangeRateEntity entity;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            // update fields but keep the same ID
            mapper.updateEntityFromDomain(exchangeRate, entity);
        } else {
            entity = mapper.toEntity(exchangeRate);
        }

        ExchangeRateEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<ExchangeRate> saveAll(List<ExchangeRate> exchangeRates) {
        List<ExchangeRateEntity> entities = exchangeRates.stream()
                .map(rate -> {
                    Optional<ExchangeRateEntity> existingOpt =
                            jpaRepository.findByBaseCurrencyAndTargetCurrency(
                                    rate.getBaseCurrency(),
                                    rate.getTargetCurrency()
                            );

                    if (existingOpt.isPresent()) {
                        ExchangeRateEntity existing = existingOpt.get();
                        mapper.updateEntityFromDomain(rate, existing);
                        return existing;
                    } else {
                        return mapper.toEntity(rate);
                    }
                })
                .collect(Collectors.toList());

        List<ExchangeRateEntity> saved = jpaRepository.saveAll(entities);
        return mapper.toDomainList(saved);
    }

    @Override
    public List<ExchangeRate> findByBaseCurrency(String baseCurrency) {
        return mapper.toDomainList(jpaRepository.findByBaseCurrency(baseCurrency));
    }

    @Override
    public Optional<ExchangeRate> findByBaseAndTarget(String baseCurrency, String targetCurrency) {
        return jpaRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByBaseCurrency(String baseCurrency) {
        jpaRepository.deleteByBaseCurrency(baseCurrency);
    }
}
