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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@RequiredArgsConstructor
@Tag(name = "Currencies", description = "APIs for managing currencies")
public class CurrencyController {

    private final CurrencyApplicationService currencyService;
    private final CurrencyApiMapper mapper;

    @Operation(summary = "Get all currencies")
    @GetMapping
    public BaseResponseApi<List<CurrencyResponse>> getAll() {
        List<CurrencyResponse> result = currencyService.getAllCurrencies()
                .stream()
                .map(mapper::toResponse)
                .toList();
        return BaseResponseApi.success(result);
    }

    @Operation(summary = "Get a currency by code")
    @GetMapping("/{code}")
    public BaseResponseApi<CurrencyResponse> getOne(@PathVariable String code) {
        return BaseResponseApi.success(
                mapper.toResponse(currencyService.getCurrency(code))
        );
    }

    @Operation(summary = "Add a new currency")
    @PostMapping
    public BaseResponseApi<CurrencyResponse> add(@Valid @RequestBody CurrencyRequest request) {
        CurrencyDto dto = mapper.toApplicationDto(request);
        return BaseResponseApi.success(
                mapper.toResponse(currencyService.addCurrency(dto))
        );
    }

    @Operation(summary = "Update an existing currency")
    @PutMapping("/{code}")
    public BaseResponseApi<CurrencyResponse> update(
            @PathVariable String code,
            @Valid @RequestBody CurrencyRequest request) {
        CurrencyDto dto = mapper.toApplicationDto(request);
        dto.setCode(code); // code comes from path
        return BaseResponseApi.success(
                mapper.toResponse(currencyService.updateCurrency(dto))
        );
    }

    @Operation(summary = "Delete a currency by code")
    @DeleteMapping("/{code}")
    public BaseResponseApi<Void> delete(@PathVariable String code) {
        currencyService.deleteCurrency(code);
        return BaseResponseApi.success(null);
    }
}

