package com.example.application.service;

import com.example.application.dto.ExchangeRateDto;

import java.util.List;

public interface ExchangeRateApplicationService {
    List<ExchangeRateDto> getRatesByBase(String baseCurrency);
    ExchangeRateDto getRatesByBaseAndQuote(String baseCurrency, String quoteCurrency);

    List<ExchangeRateDto> getLiveRatesByBase(String baseCurrency);
    ExchangeRateDto getLiveRatesByBaseAndQuote(String baseCurrency, String quoteCurrency);
    void saveOrUpdate(ExchangeRateDto dto);
    void saveOrUpdateAll(List<ExchangeRateDto> dtos);
}
