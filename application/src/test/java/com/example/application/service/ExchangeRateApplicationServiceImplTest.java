package com.example.application.service;

import com.example.application.dto.ExchangeRateDto;
import com.example.application.mapper.ExchangeRateApplicationMapper;
import com.example.application.provider.ExchangeRateApiClientProvider;
import com.example.application.provider.ExchangeRateRepositoryProvider;
import com.example.application.service.impl.ExchangeRateApplicationServiceImpl;
import com.example.domain.model.ExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateApplicationServiceImplTest {

    @Mock
    private ExchangeRateRepositoryProvider exchangeRateRepository;

    @Mock
    private ExchangeRateApiClientProvider exchangeRateApiClient;

    @Mock
    private ExchangeRateApplicationMapper mapper;

    @InjectMocks
    private ExchangeRateApplicationServiceImpl service;

    private ExchangeRate model;
    private ExchangeRateDto dto;

    @BeforeEach
    void setUp() {
        model = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        dto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(model.getRate())
                .updatedTime(model.getUpdatedTime())
                .build();
    }

    @Test
    void getRatesByBase_shouldReturnListOfDtos() {
        when(exchangeRateRepository.findByBaseCurrency("USD")).thenReturn(List.of(model));
        when(mapper.toDto(model)).thenReturn(dto);

        List<ExchangeRateDto> result = service.getRatesByBase("USD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBaseCurrency()).isEqualTo("USD");
        verify(exchangeRateRepository).findByBaseCurrency("USD");
    }

    @Test
    void getRatesByBaseAndQuote_shouldReturnDto() {
        when(exchangeRateRepository.findByBaseAndTarget("USD", "EUR")).thenReturn(Optional.of(model));
        when(mapper.toDto(model)).thenReturn(dto);

        ExchangeRateDto result = service.getRatesByBaseAndQuote("USD", "EUR");

        assertThat(result.getTargetCurrency()).isEqualTo("EUR");
        verify(exchangeRateRepository).findByBaseAndTarget("USD", "EUR");
    }

    @Test
    void getRatesByBaseAndQuote_shouldThrowIfNotFound() {
        when(exchangeRateRepository.findByBaseAndTarget("USD", "JPY")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRatesByBaseAndQuote("USD", "JPY"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Exchange rate not found for USD→JPY");
    }

    @Test
    void getLiveRatesByBase_shouldReturnDtos() {
        when(exchangeRateApiClient.fetchRates("USD")).thenReturn(List.of(model));
        when(mapper.toDto(List.of(model))).thenReturn(List.of(dto));

        List<ExchangeRateDto> result = service.getLiveRatesByBase("USD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRate()).isEqualTo(model.getRate());
        verify(exchangeRateApiClient).fetchRates("USD");
    }

    @Test
    void getLiveRatesByBaseAndQuote_shouldReturnDto() {
        when(exchangeRateApiClient.fetchRates("USD", "EUR")).thenReturn(model);
        when(mapper.toDto(model)).thenReturn(dto);

        ExchangeRateDto result = service.getLiveRatesByBaseAndQuote("USD", "EUR");

        assertThat(result.getRate()).isEqualTo(model.getRate());
        verify(exchangeRateApiClient).fetchRates("USD", "EUR");
    }

    @Test
    void saveOrUpdate_shouldMapAndSave() {
        when(mapper.toDomain(dto)).thenReturn(model);

        service.saveOrUpdate(dto);

        verify(exchangeRateRepository).save(model);
    }

    @Test
    void saveOrUpdateAll_shouldMapAndSaveAll() {
        when(mapper.toDomain(dto)).thenReturn(model);

        service.saveOrUpdateAll(List.of(dto));

        verify(exchangeRateRepository).saveAll(List.of(model));
    }

    @Test
    void saveOrUpdateAll_shouldDoNothingWhenEmpty() {
        service.saveOrUpdateAll(List.of());

        verify(exchangeRateRepository, never()).saveAll(any());
    }
}
