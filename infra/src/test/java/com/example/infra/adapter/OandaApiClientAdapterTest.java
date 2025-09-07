package com.example.infra.adapter;

import com.example.application.provider.CurrencyRepositoryProvider;
import com.example.domain.model.Currency;
import com.example.domain.model.ExchangeRate;
import com.example.infra.external.adapter.OandaApiClientAdapter;
import com.example.infra.external.client.OandaApiClient;
import com.example.infra.external.dto.OandaApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OandaApiClientAdapterTest {

    @Mock
    private OandaApiClient feignClient;

    @Mock
    private CurrencyRepositoryProvider currencyRepository;

    private Executor directExecutor;

    private OandaApiClientAdapter adapter;

    @BeforeEach
    void setUp() {
        directExecutor = Runnable::run; // run tasks synchronously in test
        adapter = new OandaApiClientAdapter(feignClient, currencyRepository, directExecutor);
    }

    private OandaApiResponse mockResponse(String base, String quote, String bid, String ask) {
        OandaApiResponse.OandaRate rate = new OandaApiResponse.OandaRate();
        rate.setBase_currency(base);
        rate.setQuote_currency(quote);
        rate.setAverage_bid(bid);
        rate.setAverage_ask(ask);
        rate.setClose_time(LocalDateTime.now().toString());

        OandaApiResponse response = new OandaApiResponse();
        response.setResponse(List.of(rate));
        return response;
    }

    @Test
    void fetchRates_shouldReturnMappedExchangeRate() {
        when(feignClient.getRates(any(), any(), any(), any(), any()))
                .thenReturn(mockResponse("USD", "EUR", "1.0", "1.2"));

        ExchangeRate rate = adapter.fetchRates("USD", "EUR");

        assertThat(rate.getBaseCurrency()).isEqualTo("USD");
        assertThat(rate.getTargetCurrency()).isEqualTo("EUR");
        assertThat(rate.getAverageBid()).isEqualTo(1.0);
        assertThat(rate.getAverageAsk()).isEqualTo(1.2);
    }

    @Test
    void fetchRates_shouldThrowWhenResponseIsEmpty() {
        when(feignClient.getRates(any(), any(), any(), any(), any()))
                .thenReturn(new OandaApiResponse()); // no response

        assertThatThrownBy(() -> adapter.fetchRates("USD", "EUR"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch exchange rates");
    }

    @Test
    void fetchRatesByBase_shouldFetchAllTargetCurrencies() {
        when(currencyRepository.findAll())
                .thenReturn(List.of(new Currency("USD", "Dollar", null, null),
                        new Currency("EUR", "Euro", null, null),
                        new Currency("JPY", "Yen", null, null)));

        when(feignClient.getRates(any(), any(), any(), any(), any()))
                .thenReturn(mockResponse("USD", "EUR", "1.0", "1.2"))
                .thenReturn(mockResponse("USD", "JPY", "110.0", "111.0"));

        List<ExchangeRate> rates = adapter.fetchRates("USD");

        assertThat(rates).hasSize(2);
        assertThat(rates.get(0).getBaseCurrency()).isEqualTo("USD");
        assertThat(rates).extracting(ExchangeRate::getTargetCurrency)
                .containsExactlyInAnyOrder("EUR", "JPY");
    }
}
