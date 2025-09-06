package com.example.application.service.impl;


import com.example.application.dto.CurrencyDto;
import com.example.application.service.CurrencyApplicationService;
import com.example.domain.model.Currency;
import com.example.domain.service.MaintainCurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyApplicationServiceImpl implements CurrencyApplicationService {

    private final MaintainCurrencyService maintainCurrencyService;
    private final CurrencyApplicationMapper mapper;

    @Override
    public List<CurrencyDto> getAllCurrencies() {
        return maintainCurrencyService.getAllCurrencies()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CurrencyDto getCurrency(String code) {
        return mapper.toDto(maintainCurrencyService.getCurrency(code));
    }

    @Override
    public CurrencyDto addCurrency(CurrencyDto dto) {
        Currency currency = mapper.toDomain(dto);
        currency.setCreatedAt(LocalDateTime.now());
        currency.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(maintainCurrencyService.addCurrency(currency));
    }

    @Override
    public CurrencyDto updateCurrency(CurrencyDto dto) {
        Currency existing = maintainCurrencyService.getCurrency(dto.getCode());
        Currency updated = new Currency(
                dto.getCode(),
                dto.getName(),
                existing.getCreatedAt(),
                LocalDateTime.now()
        );
        return mapper.toDto(maintainCurrencyService.updateCurrency(updated));
    }

    @Override
    public void deleteCurrency(String code) {
        maintainCurrencyService.deleteCurrency(code);
    }
}