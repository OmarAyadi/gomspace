package com.example.gomspace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CurrencyExchange {

    private String currency;

    private String toCurrency;

    @Builder.Default
    private double rate = 1;

    private double amount;

    private double convertedAmount;

    public static CurrencyExchange defaultCurrencyExchange(final String currency, final double amount) {
        return CurrencyExchange.builder()
                .currency(currency)
                .toCurrency(currency)
                .amount(amount)
                .convertedAmount(amount)
                .build();
    }
}
