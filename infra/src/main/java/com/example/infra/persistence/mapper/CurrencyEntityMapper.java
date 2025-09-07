package com.example.infra.persistence.mapper;

import com.example.domain.model.Currency;
import com.example.infra.persistence.entity.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CurrencyEntityMapper {

    Currency toDomain(CurrencyEntity entity);

    CurrencyEntity toEntity(Currency domain);

    List<Currency> toDomainList(List<CurrencyEntity> entities);

    List<CurrencyEntity> toEntityList(List<Currency> domains);
}
