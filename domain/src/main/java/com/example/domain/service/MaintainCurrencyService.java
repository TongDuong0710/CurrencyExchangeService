package com.example.domain.service;

import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResponseCode;
import com.example.domain.model.Currency;
import com.example.domain.ports.CurrencyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintainCurrencyService {
    private final CurrencyRepositoryPort currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency getCurrency(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new DomainException(ResponseCode.CURRENCY_NOT_FOUND));
    }

    public Currency addCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency updateCurrency(Currency currency) {
        getCurrency(currency.getCode()); // ensure exists
        return currencyRepository.save(currency);
    }

    public void deleteCurrency(String code) {
        getCurrency(code); // ensure exists
        currencyRepository.deleteByCode(code);
    }
}
