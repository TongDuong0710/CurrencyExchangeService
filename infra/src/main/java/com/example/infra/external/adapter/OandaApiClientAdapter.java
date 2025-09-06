package com.example.infra.external.adapter;


import com.example.application.provider.ExchangeRateApiClientProvider;
import com.example.domain.model.ExchangeRate;
import com.example.infra.external.client.OandaApiClient;
import com.example.infra.external.dto.OandaApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OandaApiClientAdapter implements ExchangeRateApiClientProvider {

    private final OandaApiClient feignClient;

    @Override
    public List<ExchangeRate> fetchRates(String baseCurrency, List<String> targetCurrencies) {
        // Feign call to OANDA
        OandaApiResponse response = feignClient.getRates(baseCurrency, null, "chart", null, null);

        if (response == null || response.getRates() == null) {
            throw new RuntimeException("Failed to fetch exchange rates from OANDA");
        }

        return response.getRates().entrySet().stream()
                .filter(entry -> targetCurrencies.contains(entry.getKey()))
                .map(entry -> new ExchangeRate(
                        baseCurrency,
                        entry.getKey(),
                        entry.getValue(),
                        LocalDateTime.now()
                ))
                .collect(Collectors.toList());
    }
}
