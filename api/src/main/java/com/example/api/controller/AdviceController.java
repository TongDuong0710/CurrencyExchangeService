package com.example.api.controller;

import com.example.api.dto.base.BaseResponseApi;
import com.example.api.dto.base.FieldError;
import com.example.application.exceptions.AppResponseCode;
import com.example.application.exceptions.ApplicationException;
import com.example.domain.exception.DomainException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<BaseResponseApi<?>> domainExceptionHandler(
            DomainException exception) {
        log.error("DomainException - Domain Exception {}", exception.getMessage());
        exception.printStackTrace();

        HttpStatus status = exception.getResponseCode().getHttpStatus();
        BaseResponseApi<?> response =
                BaseResponseApi.error(
                        exception.getResponseCode().getCode(),
                        exception.getMessage(),
                        exception.getPayload());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<BaseResponseApi<?>> applicationExceptionHandler(
            ApplicationException exception) {
        log.error("applicationExceptionHandler - Application Exception {}", exception.getMessage());
        exception.printStackTrace();
        HttpStatus status = exception.getResponseCode().getHttpStatus();
        BaseResponseApi<?> response =
                BaseResponseApi.error(
                        exception.getResponseCode().getCode(),
                        exception.getMessage(),
                        exception.getPayload());
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> constraintViolationHandler(ConstraintViolationException exception) {
        log.error("ConstraintViolationException - Server side exception {}", exception.getMessage());
        exception.printStackTrace();

        List<FieldError> fieldErrors =
                exception.getConstraintViolations().stream()
                        .map(
                                violation -> {
                                    String field = violation.getPropertyPath().toString();
                                    String message = violation.getMessage();
                                    return new FieldError(field, message);
                                })
                        .collect(Collectors.toList());

        return BaseResponseApi.error("INVALID_REQUEST", "Validation failed", fieldErrors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponseApi<?> internalExceptionHandler(Exception exception) {
        log.error("INTERNAL_SERVER_ERROR - Internal server error exception {}", exception.getMessage());
        exception.printStackTrace();

        return BaseResponseApi.error(
                AppResponseCode.SERVER_ERROR.getCode(),
                AppResponseCode.SERVER_ERROR.name());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> httpMessageNotReadableHandler(
            HttpMessageNotReadableException exception) {
        log.error("HttpMessageNotReadableException - Server side exception {}", exception.getMessage());
        exception.printStackTrace();

        return BaseResponseApi.error("INVALID_REQUEST", exception.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> methodArgumentNotValidHandler(
            MethodArgumentNotValidException exception) {
        log.error("MethodArgumentNotValidException - Server side exception {}", exception.getMessage());
        exception.printStackTrace();

        List<FieldError> fieldErrors =
                exception.getFieldErrors().stream()
                        .map(itm -> new FieldError(itm.getField(), itm.getDefaultMessage()))
                        .toList();
        return BaseResponseApi.error("INVALID_REQUEST", exception.getBody().getDetail(), fieldErrors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponseApi<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException - {}", ex.getMessage());
        ex.printStackTrace();

        String message = "Data integrity violation";

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            message = ex.getCause().getMessage();
        }

        return BaseResponseApi.error("INVALID_REQUEST", message, null);
    }

}
