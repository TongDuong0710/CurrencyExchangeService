package com.example.infra.external.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OandaApiResponse {
    private String base;
    private Map<String, Double> rates;
    private String timestamp;
}