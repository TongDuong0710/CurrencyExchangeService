package com.example.api.controller;

import com.example.api.dto.base.BaseResponseApi;
import com.example.api.dto.response.ExchangeRateResponse;
import com.example.api.mapper.ExchangeRateApiMapper;
import com.example.application.service.ExchangeRateApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates", description = "APIs for managing exchange rates")
public class ExchangeRateController {

    private final ExchangeRateApplicationService exchangeRateService;
    private final ExchangeRateApiMapper mapper;

    @Operation(summary = "Get exchange rates by base currency")
    @GetMapping("/{baseCurrency}")
    public BaseResponseApi<List<ExchangeRateResponse>> getRates(@PathVariable String baseCurrency) {
        List<ExchangeRateResponse> result = exchangeRateService.getRatesByBase(baseCurrency)
                .stream()
                .map(mapper::toResponse)
                .toList();
        return BaseResponseApi.success(result);
    }

    @Operation(summary = "Synchronize exchange rates from OANDA API")
    @PostMapping("/sync/{baseCurrency}")
    public BaseResponseApi<Void> syncRates(
            @PathVariable String baseCurrency,
            @RequestBody List<String> targetCurrencies) {
        exchangeRateService.syncExchangeRates(baseCurrency, targetCurrencies);
        return BaseResponseApi.success(null);
    }
}
