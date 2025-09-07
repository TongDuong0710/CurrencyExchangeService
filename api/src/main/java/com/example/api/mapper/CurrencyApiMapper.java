package com.example.api.mapper;

import com.example.api.dto.request.CurrencyRequest;
import com.example.api.dto.response.CurrencyResponse;
import com.example.application.dto.CurrencyDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrencyApiMapper {
    CurrencyDto toApplicationDto(CurrencyRequest request);
    CurrencyResponse toResponse(CurrencyDto dto);
    List<CurrencyResponse> toResponse(List<CurrencyDto> dto);
}
