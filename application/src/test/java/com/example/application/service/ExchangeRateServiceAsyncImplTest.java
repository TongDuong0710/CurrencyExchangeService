package com.example.application.service;

import com.example.application.dto.ExchangeRateDto;
import com.example.application.service.impl.ExchangeRateApplicationServiceImpl;
import com.example.application.service.impl.ExchangeRateServiceAsyncImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceAsyncImplTest {

    @Mock
    private ExchangeRateApplicationServiceImpl exchangeRateService;

    @InjectMocks
    private ExchangeRateServiceAsyncImpl asyncService;

    private ExchangeRateDto dto;

    @BeforeEach
    void setUp() {
        dto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(1.1)
                .updatedTime(LocalDateTime.now())
                .build();
    }

    @Test
    void fetchAndSaveRates_shouldCallServiceAndSave() {
        when(exchangeRateService.getLiveRatesByBase("USD")).thenReturn(List.of(dto));

        CompletableFuture<Void> result = asyncService.fetchAndSaveRates("USD");

        assertThat(result).isCompleted();
        verify(exchangeRateService).getLiveRatesByBase("USD");
        verify(exchangeRateService).saveOrUpdateAll(List.of(dto));
    }

    @Test
    void fetchAndSaveRates_shouldHandleException() {
        when(exchangeRateService.getLiveRatesByBase("USD"))
                .thenThrow(new RuntimeException("API failed"));

        CompletableFuture<Void> result = asyncService.fetchAndSaveRates("USD");

        assertThat(result).isCompleted();
        verify(exchangeRateService).getLiveRatesByBase("USD");
        verify(exchangeRateService, never()).saveOrUpdateAll(any());
    }
}
