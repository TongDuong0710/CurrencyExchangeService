package com.example.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyRequest {
    @NotBlank
    private String code;

    @NotBlank
    private String name;
}
