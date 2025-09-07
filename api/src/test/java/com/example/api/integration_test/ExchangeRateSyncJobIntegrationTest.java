package com.example.api.integration_test;

import com.example.api.CurrencyExchangeApplication;
import com.example.application.dto.CurrencyDto;
import com.example.application.job.ExchangeRateSyncJob;
import com.example.application.service.CurrencyApplicationService;
import com.example.application.service.ExchangeRateApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CurrencyExchangeApplication.class)
@ActiveProfiles("test")
class ExchangeRateSyncJobIntegrationTest {

    @Autowired
    private ExchangeRateSyncJob job;

    @Autowired
    private ExchangeRateApplicationService exchangeRateService;

    @Autowired
    private CurrencyApplicationService currencyService;

    @BeforeEach
    void setup() {
        // preload test data into H2
        currencyService.addCurrency(
                CurrencyDto.builder().code("USD").name("US Dollar").build()
        );
        currencyService.addCurrency(
                CurrencyDto.builder().code("EUR").name("Euro").build()
        );
    }

    @Test
    void jobShouldFetchAndSaveRates() {
        // run job manually
        job.syncExchangeRatesAsync();

        // verify results in DB
        var usdRates = exchangeRateService.getRatesByBase("USD");
        assertThat(usdRates).isNotEmpty();
        assertThat(usdRates.get(0).getBaseCurrency()).isEqualTo("USD");
    }
}
