package com.example.application.exceptions;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final AppResponseCode responseCode;
    private final Object payload;

    public ApplicationException(AppResponseCode responseCode) {
        super(responseCode.getCode());
        this.responseCode = responseCode;
        this.payload = null;
    }

    public ApplicationException(AppResponseCode responseCode, String message) {
        super(message, null);
        this.responseCode = responseCode;
        this.payload = null;
    }

    public ApplicationException(AppResponseCode responseCode, Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
        this.payload = null;
    }

    public ApplicationException(
            AppResponseCode responseCode, Throwable cause, Object payload) {
        super(responseCode.getCode(), cause);
        this.responseCode = responseCode;
        this.payload = payload;
    }

    public ApplicationException(AppResponseCode responseCode, Object data) {
        super(responseCode.getCode());
        this.responseCode = responseCode;
        this.payload = data;
    }

}
