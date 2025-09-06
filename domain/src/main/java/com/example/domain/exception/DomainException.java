package com.example.domain.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final ResponseCode responseCode;
    private final Object payload;
    
    public DomainException(ResponseCode responseCode) {
        super(responseCode.getCode());
        this.responseCode = responseCode;
        this.payload = null;
    }

    public DomainException(ResponseCode responseCode, String message) {
        super(message, null);
        this.responseCode = responseCode;
        this.payload = null;
    }

    public DomainException(ResponseCode responseCode, Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
        this.payload = null;
    }

    public DomainException(
            ResponseCode responseCode, Throwable cause, Object payload) {
        super(responseCode.getCode(), cause);
        this.responseCode = responseCode;
        this.payload = payload;
    }

    public DomainException(ResponseCode responseCode, Object data) {
        super(responseCode.getCode());
        this.responseCode = responseCode;
        this.payload = data;
    }

}
