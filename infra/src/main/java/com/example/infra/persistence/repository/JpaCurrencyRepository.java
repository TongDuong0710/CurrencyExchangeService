package com.example.infra.persistence.repository;

import com.example.infra.persistence.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCurrencyRepository extends JpaRepository<CurrencyEntity, String> {
}
