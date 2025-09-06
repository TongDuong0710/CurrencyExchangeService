package com.example.infra.persistence.repository;

import com.example.infra.persistence.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {
    List<ExchangeRateEntity> findByBaseCurrency(String baseCurrency);
}