package com.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private String baseCurrency;
    private String targetCurrency;
    private double averageBid;
    private double averageAsk;
    private LocalDateTime updatedTime;

    /**
     * Mid-market rate (average of bid and ask).
     */
    public double getRate() {
        return (averageBid + averageAsk) / 2.0;
    }
}