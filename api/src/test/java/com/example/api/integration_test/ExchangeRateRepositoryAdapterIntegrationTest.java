package com.example.api.integration_test;

import com.example.api.CurrencyExchangeApplication;
import com.example.domain.model.ExchangeRate;
import com.example.infra.persistence.adapter.ExchangeRateRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CurrencyExchangeApplication.class)
@ActiveProfiles("test")
class ExchangeRateRepositoryAdapterIntegrationTest {

    @Autowired
    private ExchangeRateRepositoryAdapter adapter;

    @Test
    void saveAndFind_shouldPersistAndRetrieveExchangeRate() {
        ExchangeRate usdEur = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRate saved = adapter.save(usdEur);

        assertThat(saved.getBaseCurrency()).isEqualTo("USD");
        assertThat(saved.getTargetCurrency()).isEqualTo("EUR");

        List<ExchangeRate> foundByBase = adapter.findByBaseCurrency("USD");
        assertThat(foundByBase).hasSize(1);
        assertThat(foundByBase.get(0).getTargetCurrency()).isEqualTo("EUR");

        var foundOpt = adapter.findByBaseAndTarget("USD", "EUR");
        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getAverageBid()).isEqualTo(1.0);
    }

    @Test
    void save_shouldUpdateIfExists() {
        ExchangeRate rate = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .averageBid(110.0)
                .averageAsk(111.0)
                .updatedTime(LocalDateTime.now())
                .build();

        adapter.save(rate);

        ExchangeRate updatedRate = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .averageBid(120.0)
                .averageAsk(121.0)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRate saved = adapter.save(updatedRate);

        assertThat(saved.getAverageBid()).isEqualTo(120.0);
        assertThat(saved.getAverageAsk()).isEqualTo(121.0);
    }

    @Test
    void saveAll_shouldInsertMultiple() {
        ExchangeRate usdEur = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        ExchangeRate usdJpy = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("JPY")
                .averageBid(110.0)
                .averageAsk(111.0)
                .updatedTime(LocalDateTime.now())
                .build();

        List<ExchangeRate> saved = adapter.saveAll(List.of(usdEur, usdJpy));

        assertThat(saved).hasSize(2);
        assertThat(adapter.findByBaseCurrency("USD")).hasSize(2);
    }

    @Test
    void deleteByBaseCurrency_shouldRemoveAllRatesForBase() {
        ExchangeRate usdEur = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        adapter.save(usdEur);
        assertThat(adapter.findByBaseCurrency("USD")).isNotEmpty();

        adapter.deleteByBaseCurrency("USD");

        assertThat(adapter.findByBaseCurrency("USD")).isEmpty();
    }
}
