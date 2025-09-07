package com.example.api.controller;

import com.example.api.dto.base.BaseResponseApi;
import com.example.api.dto.request.CurrencyRequest;
import com.example.api.dto.response.CurrencyResponse;
import com.example.api.mapper.CurrencyApiMapper;
import com.example.application.dto.CurrencyDto;
import com.example.application.service.CurrencyApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Currencies", description = "APIs for managing currencies")
public class CurrencyController {

    private final CurrencyApplicationService currencyService;
    private final CurrencyApiMapper mapper;
    private final MessageSource messageSource;

    @Operation(summary = "Get all currencies")
    @GetMapping
    public BaseResponseApi<List<CurrencyResponse>> getAll() {
        var result = currencyService.getAllCurrencies()
                .stream()
                .map(mapper::toResponse)
                .toList();

        return BaseResponseApi.success(result, messageSource);
    }

    @Operation(summary = "Get a currency by code")
    @GetMapping("/{code}")
    public BaseResponseApi<CurrencyResponse> getOne(@PathVariable String code) {
        var dto = currencyService.getCurrency(code);
        return BaseResponseApi.success(mapper.toResponse(dto), messageSource);
    }

    @Operation(summary = "Add a new currency")
    @PostMapping
    public BaseResponseApi<CurrencyResponse> add(@Valid @RequestBody CurrencyRequest request) {
        CurrencyDto dto = mapper.toApplicationDto(request);
        var saved = currencyService.addCurrency(dto);
        return BaseResponseApi.success(mapper.toResponse(saved), messageSource);
    }

    @Operation(summary = "Update an existing currency")
    @PutMapping("/{code}")
    public BaseResponseApi<CurrencyResponse> update(
            @PathVariable String code,
            @Valid @RequestBody CurrencyRequest request) {
        CurrencyDto dto = mapper.toApplicationDto(request);
        dto.setCode(code);
        var updated = currencyService.updateCurrency(dto);
        return BaseResponseApi.success(mapper.toResponse(updated), messageSource);
    }

    @Operation(summary = "Delete a currency by code")
    @DeleteMapping("/{code}")
    public BaseResponseApi<Void> delete(@PathVariable String code) {
        currencyService.deleteCurrency(code);
        return BaseResponseApi.success(null, messageSource);
    }
}
