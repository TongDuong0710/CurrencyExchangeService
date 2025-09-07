package com.example.api.mapper;

import com.example.api.dto.response.ExchangeRateResponse;
import com.example.application.configurations.MapStructCentralConfig;
import com.example.application.dto.ExchangeRateDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", config = MapStructCentralConfig.class)
public interface ExchangeRateApiMapper {
    ExchangeRateResponse toResponse(ExchangeRateDto dto);
    List<ExchangeRateResponse> toResponse(List<ExchangeRateDto> dtos);

}
