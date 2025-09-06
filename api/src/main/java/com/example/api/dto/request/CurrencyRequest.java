package com.example.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CurrencyRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
