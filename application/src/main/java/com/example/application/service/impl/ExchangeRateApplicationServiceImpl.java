package com.example.application.service.impl;


import com.example.application.dto.ExchangeRateDto;
import com.example.application.mapper.ExchangeRateApplicationMapper;
import com.example.application.service.ExchangeRateApplicationService;
import com.example.domain.ports.ExchangeRateRepositoryPort;
import com.example.domain.service.SyncExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateApplicationServiceImpl implements ExchangeRateApplicationService {

    private final SyncExchangeRateService syncExchangeRateService;
    private final ExchangeRateRepositoryPort exchangeRateRepository;
    private final ExchangeRateApplicationMapper mapper;

    @Override
    public void syncExchangeRates(String baseCurrency, List<String> targetCurrencies) {
        syncExchangeRateService.syncRates(baseCurrency, targetCurrencies);
    }

    @Override
    public List<ExchangeRateDto> getRatesByBase(String baseCurrency) {
        return exchangeRateRepository.findByBaseCurrency(baseCurrency)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}