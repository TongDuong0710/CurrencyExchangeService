package com.example.application.mapper;

import com.example.application.dto.CurrencyDto;
import com.example.domain.model.Currency;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyApplicationMapper {
    Currency toDomain(CurrencyDto dto);
    CurrencyDto toDto(Currency domain);
    List<CurrencyDto> toDto(List<Currency> domain);

}
