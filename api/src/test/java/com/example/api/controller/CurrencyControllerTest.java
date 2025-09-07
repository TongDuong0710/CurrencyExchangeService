package com.example.api.controller;

import com.example.api.dto.request.CurrencyRequest;
import com.example.api.dto.response.CurrencyResponse;
import com.example.api.mapper.CurrencyApiMapper;
import com.example.application.dto.CurrencyDto;
import com.example.application.service.CurrencyApplicationService;
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

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CurrencyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CurrencyApplicationService currencyService;

    @Mock
    private CurrencyApiMapper mapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    void setUp() {
        // Build MockMvc with the controller under test
        mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();
    }

    @Test
    void getAll_shouldReturnListOfCurrencies() throws Exception {
        CurrencyDto dto = CurrencyDto.builder()
                .code("USD")
                .name("Dollar")
                .build();

        CurrencyResponse response = CurrencyResponse.builder()
                .code("USD")
                .name("Dollar")
                .build();

        when(currencyService.getAllCurrencies()).thenReturn(List.of(dto));
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class)))
                .thenReturn("Success");

        mockMvc.perform(get("/api/v1/currencies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data[0].code").value("USD"))
                .andExpect(jsonPath("$.data[0].name").value("Dollar"));
    }

    @Test
    void add_shouldCreateCurrency() throws Exception {
        CurrencyRequest request = CurrencyRequest.builder()
                .code("JPY")
                .name("Yen")
                .build();

        CurrencyDto dto = CurrencyDto.builder()
                .code("JPY")
                .name("Yen")
                .build();

        CurrencyResponse response = CurrencyResponse.builder()
                .code("JPY")
                .name("Yen")
                .build();

        when(mapper.toApplicationDto(any(CurrencyRequest.class))).thenReturn(dto);
        when(currencyService.addCurrency(dto)).thenReturn(dto);
        when(mapper.toResponse(dto)).thenReturn(response);
        when(messageSource.getMessage(any(), any(), any(Locale.class)))
                .thenReturn("Success");

        mockMvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"JPY\", \"name\": \"Yen\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("JPY"))
                .andExpect(jsonPath("$.data.name").value("Yen"));
    }
}
