package com.example.infra.persistence.repository;

import com.example.infra.persistence.entity.CurrencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCurrencyRepository extends JpaRepository<CurrencyEntity, String> {
}
