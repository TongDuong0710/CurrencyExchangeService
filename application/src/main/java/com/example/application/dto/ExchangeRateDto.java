package com.example.application.dto;

import lombok.Data;

@Data
public class ExchangeRateDto {
    private String baseCurrency;
    private String targetCurrency;
    private double rate;
}
