package com.example.infra.persistence.mapper;

import com.example.application.dto.ExchangeRateDto;
import com.example.infra.persistence.entity.ExchangeRateEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRatePersistenceMapper {
    ExchangeRateDto toDto(ExchangeRateEntity entity);
    ExchangeRateEntity toEntity(ExchangeRateDto dto);
}
