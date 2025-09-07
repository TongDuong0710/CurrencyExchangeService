package com.example.application.service;

import com.example.application.dto.CurrencyDto;
import com.example.application.mapper.CurrencyApplicationMapper;
import com.example.application.service.impl.CurrencyApplicationServiceImpl;
import com.example.domain.model.Currency;
import com.example.domain.service.MaintainCurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyApplicationServiceImplTest {

    @Mock
    private MaintainCurrencyService maintainCurrencyService;

    @Mock
    private CurrencyApplicationMapper mapper;

    @InjectMocks
    private CurrencyApplicationServiceImpl service;

    private Currency currency;
    private CurrencyDto dto;

    @BeforeEach
    void setUp() {
        currency = new Currency("USD", "Dollar", LocalDateTime.now().minusDays(1), LocalDateTime.now());
        dto = CurrencyDto.builder()
                .code("USD")
                .name("Dollar")
                .createdAt(currency.getCreatedAt())
                .updatedAt(currency.getUpdatedAt())
                .build();
    }

    @Test
    void getAllCurrencies_shouldReturnDtoList() {
        when(maintainCurrencyService.getAllCurrencies()).thenReturn(List.of(currency));
        when(mapper.toDto(List.of(currency))).thenReturn(List.of(dto));

        List<CurrencyDto> result = service.getAllCurrencies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("USD");
        verify(maintainCurrencyService).getAllCurrencies();
    }

    @Test
    void getCurrency_shouldReturnDto() {
        when(maintainCurrencyService.getCurrency("USD")).thenReturn(currency);
        when(mapper.toDto(currency)).thenReturn(dto);

        CurrencyDto result = service.getCurrency("USD");

        assertThat(result.getCode()).isEqualTo("USD");
        assertThat(result.getName()).isEqualTo("Dollar");
        verify(maintainCurrencyService).getCurrency("USD");
    }

    @Test
    void addCurrency_shouldSaveAndReturnDto() {
        when(mapper.toDomain(dto)).thenReturn(currency);
        when(maintainCurrencyService.addCurrency(currency)).thenReturn(currency);
        when(mapper.toDto(currency)).thenReturn(dto);

        CurrencyDto result = service.addCurrency(dto);

        assertThat(result.getCode()).isEqualTo("USD");
        verify(maintainCurrencyService).addCurrency(currency);
    }

    @Test
    void updateCurrency_shouldUpdateAndReturnDto() {
        Currency updated = new Currency("USD", "Dollar Updated",
                currency.getCreatedAt(), LocalDateTime.now());

        CurrencyDto updateDto = CurrencyDto.builder()
                .code("USD")
                .name("Dollar Updated")
                .build();

        when(maintainCurrencyService.getCurrency("USD")).thenReturn(currency);
        when(maintainCurrencyService.updateCurrency(any(Currency.class))).thenReturn(updated);
        when(mapper.toDto(updated)).thenReturn(
                CurrencyDto.builder()
                        .code("USD")
                        .name("Dollar Updated")
                        .createdAt(updated.getCreatedAt())
                        .updatedAt(updated.getUpdatedAt())
                        .build()
        );

        CurrencyDto result = service.updateCurrency(updateDto);

        assertThat(result.getName()).isEqualTo("Dollar Updated");
        verify(maintainCurrencyService).updateCurrency(any(Currency.class));
    }

    @Test
    void deleteCurrency_shouldCallDomainService() {
        doNothing().when(maintainCurrencyService).deleteCurrency("USD");

        service.deleteCurrency("USD");

        verify(maintainCurrencyService).deleteCurrency("USD");
    }
}
