package com.example.api.dto.base;

public record BaseResponseMeta(Long timestamp) {

  public static BaseResponseMeta generate() {
    return new BaseResponseMeta(System.currentTimeMillis());
  }
}
