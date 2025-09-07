package com.example.application.job;

import com.example.application.dto.CurrencyDto;
import com.example.application.service.CurrencyApplicationService;
import com.example.application.service.ExchangeRateApplicationService;
import com.example.application.service.ExchangeRateAsyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateSyncJobUnitTest {

    @Mock
    private ExchangeRateApplicationService exchangeRateService;

    @Mock
    private CurrencyApplicationService currencyService;

    @Mock
    private ExchangeRateAsyncService exchangeRateAsyncService;

    @InjectMocks
    private ExchangeRateSyncJob job;

    private CurrencyDto usd;

    @BeforeEach
    void setUp() {
        usd = CurrencyDto.builder().code("USD").name("Dollar").build();
    }

    @Test
    void shouldSkipWhenNoCurrencies() {
        when(currencyService.getAllCurrencies()).thenReturn(List.of());

        job.syncExchangeRatesAsync();

        verify(exchangeRateAsyncService, never()).fetchAndSaveRates(anyString());
    }

    @Test
    void shouldRunAsyncForEachCurrency() {
        when(currencyService.getAllCurrencies()).thenReturn(List.of(usd));
        when(exchangeRateAsyncService.fetchAndSaveRates("USD"))
                .thenReturn(CompletableFuture.completedFuture(null));

        job.syncExchangeRatesAsync();

        verify(exchangeRateAsyncService).fetchAndSaveRates("USD");
    }
}
