package com.example.domain.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResponseCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DM-5000", "error.internal_server"),

    // Currency-related errors
    CURRENCY_NOT_FOUND(HttpStatus.BAD_REQUEST, "DM-4000", "error.currency_not_found"),
    INVALID_CURRENCY_CODE(HttpStatus.BAD_REQUEST, "DM-4001", "error.invalid_currency_code"),
    EXCHANGE_RATE_NOT_AVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "DM-4002", "error.exchange_rate_not_available");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    private static final Map<String, ResponseCode> CODE_MAP = new HashMap<>();

    static {
        for (ResponseCode responseCode : values()) {
            CODE_MAP.put(responseCode.code, responseCode);
        }
    }
}
