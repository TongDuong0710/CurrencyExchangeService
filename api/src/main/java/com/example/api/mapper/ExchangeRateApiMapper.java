package com.example.api.mapper;

import com.example.api.dto.response.ExchangeRateResponse;
import com.example.application.dto.ExchangeRateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExchangeRateApiMapper {
    ExchangeRateResponse toResponse(ExchangeRateDto dto);
}
