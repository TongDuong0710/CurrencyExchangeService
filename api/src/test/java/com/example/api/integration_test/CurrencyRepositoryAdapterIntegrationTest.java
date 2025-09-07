package com.example.api.integration_test;

import com.example.api.CurrencyExchangeApplication;
import com.example.domain.model.Currency;
import com.example.infra.persistence.adapter.CurrencyRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CurrencyExchangeApplication.class) // load full Spring context
@ActiveProfiles("test")
class CurrencyRepositoryAdapterIntegrationTest {

    @Autowired
    private CurrencyRepositoryAdapter adapter;

    @Test
    void saveAndFind_shouldPersistCurrency() {
        Currency currency = new Currency("USD", "Dollar", LocalDateTime.now(), LocalDateTime.now());

        // save
        Currency saved = adapter.save(currency);
        assertThat(saved.getCode()).isEqualTo("USD");

        // findByCode
        Currency found = adapter.findByCode("USD").orElseThrow();
        assertThat(found.getName()).isEqualTo("Dollar");

        // findAll
        assertThat(adapter.findAll()).hasSize(1);

        // delete
        adapter.deleteByCode("USD");
        assertThat(adapter.findAll()).isEmpty();
    }
}
