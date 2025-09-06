package com.example.application.mapper;

import com.example.application.dto.ExchangeRateDto;
import com.example.domain.model.ExchangeRate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRateApplicationMapper {
    ExchangeRate toDomain(ExchangeRateDto dto);
    ExchangeRateDto toDto(ExchangeRate domain);
}
