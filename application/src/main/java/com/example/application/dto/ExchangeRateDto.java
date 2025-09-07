package com.example.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExchangeRateDto {
    private String baseCurrency;
    private String targetCurrency;
    private double rate;
    private double averageBid;
    private double averageAsk;
    private LocalDateTime updatedTime;
}
