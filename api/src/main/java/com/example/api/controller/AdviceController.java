package com.example.api.controller;

import com.example.api.dto.base.BaseResponseApi;
import com.example.api.dto.base.FieldError;
import com.example.application.exceptions.AppResponseCode;
import com.example.application.exceptions.ApplicationException;
import com.example.domain.exception.DomainException;
import com.example.domain.exception.ResponseCode;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AdviceController {

    private final MessageSource messageSource;

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseResponseApi<?>> handleDomainException(DomainException exception) {
        log.error("DomainException - {}", exception.getMessage(), exception);
        ResponseCode code = exception.getResponseCode();

        BaseResponseApi<?> response =
                BaseResponseApi.error(code.getCode(), code.getMessageKey(), exception.getPayload(), messageSource);

        return new ResponseEntity<>(response, code.getHttpStatus());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponseApi<?>> handleApplicationException(ApplicationException exception) {
        log.error("ApplicationException - {}", exception.getMessage(), exception);
        AppResponseCode code = exception.getResponseCode();

        BaseResponseApi<?> response =
                BaseResponseApi.error(code.getCode(), code.getMessageKey(), exception.getPayload(), messageSource);

        return new ResponseEntity<>(response, code.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> handleConstraintViolation(ConstraintViolationException exception) {
        log.error("ConstraintViolationException - {}", exception.getMessage(), exception);

        List<FieldError> fieldErrors = exception.getConstraintViolations().stream()
                .map(v -> new FieldError(v.getPropertyPath().toString(), v.getMessage()))
                .toList();

        return BaseResponseApi.error("INVALID_REQUEST", "error.validation_failed", fieldErrors, messageSource);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        log.error("HttpMessageNotReadableException - {}", exception.getMessage(), exception);
        return BaseResponseApi.error("INVALID_REQUEST", "error.invalid_request", messageSource);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException - {}", exception.getMessage(), exception);

        List<FieldError> fieldErrors = exception.getFieldErrors().stream()
                .map(itm -> new FieldError(itm.getField(), itm.getDefaultMessage()))
                .toList();

        return BaseResponseApi.error("INVALID_REQUEST", "error.validation_failed", fieldErrors, messageSource);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException - {}", ex.getMessage(), ex);

        // fallback to i18n key if no DB-specific message
        String messageKey = (ex.getCause() != null && ex.getCause().getMessage() != null)
                ? ex.getCause().getMessage()
                : "error.data_integrity";

        return BaseResponseApi.error("INVALID_REQUEST", messageKey, messageSource);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponseApi<?> handleNoResourceFound(NoResourceFoundException ex) {
        log.debug("No resource found: {}", ex.getMessage());
        return BaseResponseApi.error("NOT_FOUND", "error.resource_not_found", messageSource);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponseApi<?> handleInternalError(Exception exception) {
        log.error("INTERNAL_SERVER_ERROR - {}", exception.getMessage(), exception);
        AppResponseCode code = AppResponseCode.SERVER_ERROR;

        return BaseResponseApi.error(code.getCode(), code.getMessageKey(), messageSource);
    }
}
