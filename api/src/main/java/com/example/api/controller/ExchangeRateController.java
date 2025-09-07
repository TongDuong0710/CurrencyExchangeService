package com.example.api.controller;

import com.example.api.dto.base.BaseResponseApi;
import com.example.api.dto.response.ExchangeRateResponse;
import com.example.api.mapper.ExchangeRateApiMapper;
import com.example.application.service.ExchangeRateApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates", description = "APIs for exchange rates (DB + Live from OANDA)")
public class ExchangeRateController {

    private final ExchangeRateApplicationService exchangeRateService;
    private final ExchangeRateApiMapper mapper;
    private final MessageSource messageSource;

    /**
     * Get all exchange rates by base currency (from DB).
     */
    @Operation(summary = "Get exchange rates by base currency (DB)")
    @GetMapping("/{baseCurrency}")
    public BaseResponseApi<List<ExchangeRateResponse>> getRatesByBase(@PathVariable String baseCurrency) {
        var result = exchangeRateService.getRatesByBase(baseCurrency)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return BaseResponseApi.success(result, messageSource);
    }

    /**
     * Get a specific exchange rate by base and quote (from DB).
     */
    @Operation(summary = "Get exchange rate by base and quote (DB)")
    @GetMapping("/{baseCurrency}/{quoteCurrency}")
    public BaseResponseApi<ExchangeRateResponse> getRateByBaseAndQuote(
            @PathVariable String baseCurrency,
            @PathVariable String quoteCurrency) {
        var dto = exchangeRateService.getRatesByBaseAndQuote(baseCurrency, quoteCurrency);
        return BaseResponseApi.success(mapper.toResponse(dto), messageSource);
    }

    /**
     * Get live exchange rates by base currency (from OANDA).
     */
    @Operation(summary = "Get live exchange rates by base currency (OANDA)")
    @GetMapping("/live/{baseCurrency}")
    public BaseResponseApi<List<ExchangeRateResponse>> getLiveRatesByBase(@PathVariable String baseCurrency) {
        var result = exchangeRateService.getLiveRatesByBase(baseCurrency)
                .stream()
                .map(mapper::toResponse)
                .toList();

        return BaseResponseApi.success(result, messageSource);
    }

    /**
     * Get live exchange rate by base and quote (from OANDA).
     */
    @Operation(summary = "Get live exchange rate by base and quote (OANDA)")
    @GetMapping("/live/{baseCurrency}/{quoteCurrency}")
    public BaseResponseApi<ExchangeRateResponse> getLiveRateByBaseAndQuote(
            @PathVariable String baseCurrency,
            @PathVariable String quoteCurrency) {
        var dto = exchangeRateService.getLiveRatesByBaseAndQuote(baseCurrency, quoteCurrency);
        return BaseResponseApi.success(mapper.toResponse(dto), messageSource);
    }
}
