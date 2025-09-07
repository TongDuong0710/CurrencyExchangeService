package com.example.api.integration_test;
import com.example.api.dto.request.CurrencyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addCurrency_thenGetAllAndDelete() throws Exception {
        // --- Add a new currency ---
        CurrencyRequest request = CurrencyRequest.builder()
                .code("USD")
                .name("Dollar")
                .build();

        mockMvc.perform(post("/api/v1/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code", is("USD")))
                .andExpect(jsonPath("$.data.name", is("Dollar")));

        // --- Get all currencies ---
        mockMvc.perform(get("/api/v1/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].code", is("USD")));

        // --- Get one currency by code ---
        mockMvc.perform(get("/api/v1/currencies/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code", is("USD")));

        // --- Update currency ---
        CurrencyRequest update = CurrencyRequest.builder()
                .code("USD")
                .name("US Dollar Updated")
                .build();

        mockMvc.perform(put("/api/v1/currencies/USD")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("US Dollar Updated")));

        // --- Delete currency ---
        mockMvc.perform(delete("/api/v1/currencies/USD"))
                .andExpect(status().isOk());

        // --- Verify empty ---
        mockMvc.perform(get("/api/v1/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}

