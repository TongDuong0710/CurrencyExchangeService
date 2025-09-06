package com.example.domain.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResponseCode{
    INTERNAL_SERVER_ERROR(BAD_REQUEST, "DM-5000", "Server error"),

    // Currency-related errors
    CURRENCY_NOT_FOUND(BAD_REQUEST, "DM-4000", "Currency not found"),
    INVALID_CURRENCY_CODE(BAD_REQUEST, "DM-4001", "Invalid currency code"),
    EXCHANGE_RATE_NOT_AVAILABLE(SERVICE_UNAVAILABLE, "DM-4002", "Exchange rate not available");

    ;
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    private static final Map<String, ResponseCode> CODE_MAP = new HashMap<>();

    static {
        for (ResponseCode responseCode : values()) {
            CODE_MAP.put(responseCode.code, responseCode);
        }
    }
}