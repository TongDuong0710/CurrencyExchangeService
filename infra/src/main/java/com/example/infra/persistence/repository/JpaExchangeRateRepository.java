package com.example.infra.persistence.repository;

import com.example.infra.persistence.entity.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface JpaExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    /**
     * Find all exchange rates by base currency.
     */
    List<ExchangeRateEntity> findByBaseCurrency(String baseCurrency);

    /**
     * Find a specific exchange rate by base and target currency.
     */
    Optional<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);

    /**
     * Delete all exchange rates for a given base currency.
     */
    @Modifying
    @Transactional
    void deleteByBaseCurrency(String baseCurrency);
}
