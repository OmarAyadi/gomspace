package com.example.gomspace.mock;

import com.example.gomspace.dto.CurrencyDto;
import com.example.gomspace.model.enums.Currency;

import java.util.Map;

import static com.example.gomspace.model.enums.Currency.*;

public interface CurrencyMock {

    String VALID_CURRENCY_MOCK = Currency.AED.name();
    Map<String, Double> CURRENCY_RATES_MOCK = Map.of(
            EUR.name(), 3D,
            USD.name(), 2D,
            ZAR.name(), 0.5D
    );

    static CurrencyDto mockCurrencyDto() {
        return CurrencyDto.builder()
                .baseCode(VALID_CURRENCY_MOCK)
                .conversionRates(CURRENCY_RATES_MOCK)
                .build();
    }
}
