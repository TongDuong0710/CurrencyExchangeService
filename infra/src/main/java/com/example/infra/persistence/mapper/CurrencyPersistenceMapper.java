package com.example.infra.persistence.mapper;

import com.example.application.dto.CurrencyDto;
import com.example.infra.persistence.entity.CurrencyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyPersistenceMapper {
    CurrencyDto toDto(CurrencyEntity entity);
    CurrencyEntity toEntity(CurrencyDto dto);
}
