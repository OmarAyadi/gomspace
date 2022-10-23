package com.example.gomspace.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CurrencyDto {
    private String result;

    @JsonAlias("base_code")
    private String baseCode;

    @JsonAlias("conversion_rates")
    private Map<String, Double> conversionRates = new HashMap<>();
}

