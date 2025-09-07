package com.example.infra.persistence.mapper;

import com.example.domain.model.ExchangeRate;
import com.example.infra.configurations.MapStructCentralConfig;
import com.example.infra.persistence.entity.ExchangeRateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", config = MapStructCentralConfig.class)
public interface ExchangeRateEntityMapper {

    ExchangeRate toDomain(ExchangeRateEntity entity);

    @Mapping(target = "id", ignore = true)
    ExchangeRateEntity toEntity(ExchangeRate domain);

    List<ExchangeRate> toDomainList(List<ExchangeRateEntity> entities);

    List<ExchangeRateEntity> toEntityList(List<ExchangeRate> domains);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "baseCurrency", ignore = true)
    @Mapping(target = "targetCurrency", ignore = true)
    void updateEntityFromDomain(ExchangeRate domain,
                                @MappingTarget ExchangeRateEntity entity);
}
