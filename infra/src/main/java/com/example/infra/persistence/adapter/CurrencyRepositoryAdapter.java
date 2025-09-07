package com.example.infra.persistence.adapter;

import com.example.application.provider.CurrencyRepositoryProvider;
import com.example.domain.model.Currency;
import com.example.infra.persistence.entity.CurrencyEntity;
import com.example.infra.persistence.mapper.CurrencyEntityMapper;
import com.example.infra.persistence.repository.JpaCurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrencyRepositoryAdapter implements CurrencyRepositoryProvider {

    private final JpaCurrencyRepository jpaRepository;
    private final CurrencyEntityMapper mapper;

    @Override
    public List<Currency> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return jpaRepository.findById(code).map(mapper::toDomain);
    }

    @Override
    public Currency save(Currency currency) {
        CurrencyEntity saved = jpaRepository.save(mapper.toEntity(currency));
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteByCode(String code) {
        jpaRepository.deleteById(code);
    }
}
