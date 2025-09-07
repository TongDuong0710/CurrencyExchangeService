package com.example.infra.external.adapter;

import com.example.application.provider.CurrencyRepositoryProvider;
import com.example.application.provider.ExchangeRateApiClientProvider;
import com.example.domain.model.Currency;
import com.example.domain.model.ExchangeRate;
import com.example.infra.external.client.OandaApiClient;
import com.example.infra.external.dto.OandaApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class OandaApiClientAdapter implements ExchangeRateApiClientProvider {

    private final OandaApiClient feignClient;
    private final CurrencyRepositoryProvider currencyRepository; // get currencies from DB
    private final Executor exchangeRateExecutor;

    @Override
    public ExchangeRate fetchRates(String baseCurrency, String targetCurrency) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate = LocalDate.now().minusDays(1).format(formatter);
        String endDate   = LocalDate.now().format(formatter);

        OandaApiResponse response = feignClient.getRates(
                baseCurrency,
                targetCurrency,
                "chart",
                startDate,
                endDate
        );

        if (response == null || response.getResponse() == null || response.getResponse().isEmpty()) {
            throw new RuntimeException("Failed to fetch exchange rates from OANDA");
        }

        // Take the latest record (last in the list)
        OandaApiResponse.OandaRate latest = response.getResponse().get(response.getResponse().size() - 1);

        double avgBid = Double.parseDouble(latest.getAverage_bid());
        double avgAsk = Double.parseDouble(latest.getAverage_ask());

        LocalDateTime updatedTime =
                LocalDateTime.parse(latest.getClose_time(), DateTimeFormatter.ISO_DATE_TIME);

        return ExchangeRate.builder()
                .baseCurrency(latest.getBase_currency())
                .targetCurrency(latest.getQuote_currency())
                .averageBid(avgBid)
                .averageAsk(avgAsk)
                .updatedTime(updatedTime)
                .build();
    }

    @Override
    public List<ExchangeRate> fetchRates(String baseCurrency) {
        List<String> targetCurrencies = currencyRepository.findAll().stream()
                .map(Currency::getCode)
                .filter(code -> !code.equalsIgnoreCase(baseCurrency))
                .toList();

        List<CompletableFuture<ExchangeRate>> futures = targetCurrencies.stream()
                .map(target -> CompletableFuture.supplyAsync(
                        () -> fetchRates(baseCurrency, target),
                        exchangeRateExecutor
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
