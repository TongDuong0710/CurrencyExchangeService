package com.example.api.dto.base;

public record BaseResponseStatus(String code, String message) {

  public BaseResponseStatus(String code) {
    this(code, null);
  }
}
