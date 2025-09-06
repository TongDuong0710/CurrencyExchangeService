package com.example.api.mapper;

import com.example.api.dto.request.CurrencyRequest;
import com.example.api.dto.response.CurrencyResponse;
import com.example.application.dto.CurrencyDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyApiMapper {
    CurrencyDto toApplicationDto(CurrencyRequest request);
    CurrencyResponse toResponse(CurrencyDto dto);
}
