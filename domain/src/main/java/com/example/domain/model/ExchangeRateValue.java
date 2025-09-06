package com.example.domain.model;

import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResponseCode;
import lombok.Getter;


@Getter
public class ExchangeRateValue {
    private final double value;

    public ExchangeRateValue(double value) {
        if (value <= 0) {
            throw new DomainException(ResponseCode.EXCHANGE_RATE_NOT_AVAILABLE);
        }
        this.value = value;
    }
}