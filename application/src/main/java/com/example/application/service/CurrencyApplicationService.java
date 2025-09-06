package com.example.application.service;

import com.example.application.dto.CurrencyDto;

import java.util.List;

public interface CurrencyApplicationService {
    List<CurrencyDto> getAllCurrencies();
    CurrencyDto getCurrency(String code);
    CurrencyDto addCurrency(CurrencyDto dto);
    CurrencyDto updateCurrency(CurrencyDto dto);
    void deleteCurrency(String code);
}
