package com.example.application.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppResponseCode {
    SUCCESS(HttpStatus.OK, "ES-0000", "app.success"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DM-5000", "error.internal_server");

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    AppResponseCode(HttpStatus httpStatus, String code, String messageKey) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
    }
}
