package com.example.application.service.impl;

import com.example.application.dto.ExchangeRateDto;
import com.example.application.mapper.ExchangeRateApplicationMapper;
import com.example.application.provider.ExchangeRateApiClientProvider;
import com.example.application.provider.ExchangeRateRepositoryProvider;
import com.example.application.service.ExchangeRateApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateApplicationServiceImpl implements ExchangeRateApplicationService {

    private final ExchangeRateRepositoryProvider exchangeRateRepository;
    private final ExchangeRateApiClientProvider exchangeRateApiClient;

    private final ExchangeRateApplicationMapper mapper;

    /**
     * Get all exchange rates for a given base currency from DB.
     */
    @Override
    public List<ExchangeRateDto> getRatesByBase(String baseCurrency) {
        return mapper.toDto(exchangeRateRepository.findByBaseCurrency(baseCurrency));
    }

    /**
     * Get a specific exchange rate (base → quote) from DB.
     */
    @Override
    public ExchangeRateDto getRatesByBaseAndQuote(String baseCurrency, String quoteCurrency) {
        return exchangeRateRepository.findByBaseAndTarget(baseCurrency, quoteCurrency)
                .map(mapper::toDto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Exchange rate not found for " + baseCurrency + "→" + quoteCurrency));
    }

    @Override
    public List<ExchangeRateDto> getLiveRatesByBase(String baseCurrency) {
        return mapper.toDto(exchangeRateApiClient.fetchRates(baseCurrency));
    }

    @Override
    public ExchangeRateDto getLiveRatesByBaseAndQuote(String baseCurrency, String quoteCurrency) {
        return mapper.toDto(exchangeRateApiClient.fetchRates(baseCurrency, quoteCurrency));
    }

    @Override
    public void saveOrUpdate(ExchangeRateDto dto) {
        var model = mapper.toDomain(dto);
        exchangeRateRepository.save(model);
    }
    @Override
    public void saveOrUpdateAll(List<ExchangeRateDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }

        var models = dtos.stream()
                .map(mapper::toDomain)
                .toList();

        exchangeRateRepository.saveAll(models); // batch insert/update
    }

}
