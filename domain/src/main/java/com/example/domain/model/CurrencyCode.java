package com.example.domain.model;


import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResponseCode;
import lombok.Getter;

@Getter
public class CurrencyCode {
    private final String code;

    public CurrencyCode(String code) {
        if (code == null || code.length() != 3) {
            throw new DomainException(ResponseCode.INVALID_CURRENCY_CODE);
        }
        this.code = code.toUpperCase();
    }
}