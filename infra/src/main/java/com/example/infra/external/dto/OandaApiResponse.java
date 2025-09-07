package com.example.infra.external.dto;

import lombok.Data;

import java.util.List;

@Data
public class OandaApiResponse {
    private List<OandaRate> response;

    @Data
    public static class OandaRate {
        private String base_currency;
        private String quote_currency;
        private String close_time;
        private String average_bid;
        private String average_ask;
        private String high_bid;
        private String high_ask;
        private String low_bid;
        private String low_ask;
    }
}