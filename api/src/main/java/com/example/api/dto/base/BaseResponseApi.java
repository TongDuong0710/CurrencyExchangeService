package com.example.api.dto.base;

import com.example.application.exceptions.AppResponseCode;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public record BaseResponseApi<T>(BaseResponseStatus status, T data, BaseResponseMeta metaData) {

    private BaseResponseApi(BaseResponseStatus status, BaseResponseMeta metaData) {
        this(status, null, metaData);
    }

    // SUCCESS
    public static <T> BaseResponseApi<T> success(T data, MessageSource messageSource) {
        String message = messageSource.getMessage(
                AppResponseCode.SUCCESS.getMessageKey(),
                null,
                LocaleContextHolder.getLocale()
        );
        return new BaseResponseApi<>(
                new BaseResponseStatus(AppResponseCode.SUCCESS.getCode(), message),
                data,
                BaseResponseMeta.generate()
        );
    }

    // ERROR (with data)
    public static <T> BaseResponseApi<T> error(String errorCode, String messageKey, T data, MessageSource messageSource) {
        String message = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return new BaseResponseApi<>(
                new BaseResponseStatus(errorCode, message),
                data,
                BaseResponseMeta.generate()
        );
    }

    // ERROR (no data)
    public static <T> BaseResponseApi<T> error(String errorCode, String messageKey, MessageSource messageSource) {
        return error(errorCode, messageKey, null, messageSource);
    }
}
