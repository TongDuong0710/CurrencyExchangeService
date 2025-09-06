package com.example.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeRateResponse {
    private String baseCurrency;
    private String targetCurrency;
    private double rate;
    private LocalDateTime timestamp;
}
