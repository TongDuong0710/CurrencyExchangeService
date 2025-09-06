package com.example.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum AppResponseCode {
    SUCCESS(OK, "ES-0000", "Success"),
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "DM-5000", "Server error"),

    NOT_FOUND(BAD_REQUEST, "APP-4000", "Not found"),
    INVALID_PARAM(BAD_REQUEST, "APP-4002", "Invalid Parameter"),
    INSUFFICIENT_STOCK(CONFLICT, "APP-0409", "Insufficient stock"),

    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    AppResponseCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
