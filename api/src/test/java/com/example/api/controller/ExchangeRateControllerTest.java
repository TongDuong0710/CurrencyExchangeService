package com.example.api.controller;

import com.example.api.dto.response.ExchangeRateResponse;
import com.example.api.mapper.ExchangeRateApiMapper;
import com.example.application.dto.ExchangeRateDto;
import com.example.application.service.ExchangeRateApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExchangeRateApplicationService exchangeRateService;

    @Mock
    private ExchangeRateApiMapper mapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController).build();
    }

    @Test
    void getRatesByBase_shouldReturnListOfRates() throws Exception {
        ExchangeRateDto dto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(1.1)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(1.1)
                .updatedTime(LocalDateTime.now())
                .build();

        when(exchangeRateService.getRatesByBase("USD")).thenReturn(List.of(dto));
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Success");

        mockMvc.perform(get("/api/v1/exchange-rates/USD"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].baseCurrency").value("USD"))
                .andExpect(jsonPath("$.data[0].targetCurrency").value("EUR"))
                .andExpect(jsonPath("$.data[0].rate").value(1.1));
    }

    @Test
    void getRateByBaseAndQuote_shouldReturnSingleRate() throws Exception {
        ExchangeRateDto dto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .rate(110.5)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .rate(110.5)
                .updatedTime(LocalDateTime.now())
                .build();

        when(exchangeRateService.getRatesByBaseAndQuote("USD", "JPY")).thenReturn(dto);
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Success");

        mockMvc.perform(get("/api/v1/exchange-rates/USD/JPY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseCurrency").value("USD"))
                .andExpect(jsonPath("$.data.targetCurrency").value("JPY"))
                .andExpect(jsonPath("$.data.rate").value(110.5));
    }

    @Test
    void getLiveRatesByBase_shouldReturnListOfRates() throws Exception {
        ExchangeRateDto dto = ExchangeRateDto.builder()
                .baseCurrency("EUR")
                .targetCurrency("GBP")
                .rate(0.85)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .baseCurrency("EUR")
                .targetCurrency("GBP")
                .rate(0.85)
                .updatedTime(LocalDateTime.now())
                .build();

        when(exchangeRateService.getLiveRatesByBase("EUR")).thenReturn(List.of(dto));
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Success");

        mockMvc.perform(get("/api/v1/exchange-rates/live/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].baseCurrency").value("EUR"))
                .andExpect(jsonPath("$.data[0].targetCurrency").value("GBP"))
                .andExpect(jsonPath("$.data[0].rate").value(0.85));
    }

    @Test
    void getLiveRateByBaseAndQuote_shouldReturnSingleRate() throws Exception {
        ExchangeRateDto dto = ExchangeRateDto.builder()
                .baseCurrency("EUR")
                .targetCurrency("USD")
                .rate(1.05)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRateResponse response = ExchangeRateResponse.builder()
                .baseCurrency("EUR")
                .targetCurrency("USD")
                .rate(1.05)
                .updatedTime(LocalDateTime.now())
                .build();

        when(exchangeRateService.getLiveRatesByBaseAndQuote("EUR", "USD")).thenReturn(dto);
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class))).thenReturn("Success");

        mockMvc.perform(get("/api/v1/exchange-rates/live/EUR/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseCurrency").value("EUR"))
                .andExpect(jsonPath("$.data.targetCurrency").value("USD"))
                .andExpect(jsonPath("$.data.rate").value(1.05));
    }
}
