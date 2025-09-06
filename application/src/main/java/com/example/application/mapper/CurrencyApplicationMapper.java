package com.example.application.mapper;

import com.example.application.dto.CurrencyDto;
import com.example.domain.model.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyApplicationMapper {
    Currency toDomain(CurrencyDto dto);
    CurrencyDto toDto(Currency domain);
}
