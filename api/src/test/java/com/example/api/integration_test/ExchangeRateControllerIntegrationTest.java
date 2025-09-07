package com.example.api.integration_test;

import com.example.domain.model.ExchangeRate;
import com.example.infra.persistence.repository.JpaExchangeRateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExchangeRateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaExchangeRateRepository exchangeRateRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupDb() {
        exchangeRateRepo.deleteAll();

        ExchangeRate rate = ExchangeRate.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .averageBid(1.0)
                .averageAsk(1.2)
                .updatedTime(LocalDateTime.now())
                .build();

        // save test data into H2 DB
        exchangeRateRepo.save(
                com.example.infra.persistence.entity.ExchangeRateEntity.builder()
                        .baseCurrency(rate.getBaseCurrency())
                        .targetCurrency(rate.getTargetCurrency())
                        .averageBid(rate.getAverageBid())
                        .averageAsk(rate.getAverageAsk())
                        .updatedTime(rate.getUpdatedTime())
                        .build()
        );
    }

    @Test
    void shouldReturnRatesByBaseFromDb() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/USD")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].baseCurrency", is("USD")))
                .andExpect(jsonPath("$.data[0].targetCurrency", is("EUR")));
    }

    @Test
    void shouldReturnRateByBaseAndQuoteFromDb() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseCurrency", is("USD")))
                .andExpect(jsonPath("$.data.targetCurrency", is("EUR")));
    }

    @Test
    void shouldReturnLiveRatesFromOanda() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/live/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void shouldReturnLiveRateByBaseAndQuoteFromOanda() throws Exception {
        mockMvc.perform(get("/api/v1/exchange-rates/live/USD/EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.baseCurrency", is("USD")))
                .andExpect(jsonPath("$.data.targetCurrency", is("EUR")));
    }
}
