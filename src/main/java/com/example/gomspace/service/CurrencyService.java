package com.example.gomspace.service;

import com.example.gomspace.model.enums.Currency;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

import static com.example.gomspace.model.ClientException.throwBadRequest;
import static com.example.gomspace.utils.Constants.CURRENCY;
import static com.example.gomspace.utils.StringTemplates.invalidValue;
import static com.example.gomspace.utils.Utils.getAndSortKeys;

@Service
@AllArgsConstructor
public class CurrencyService {
    private CurrencyExternalService currencyExternalService;

    public Double getCurrencyRate(final @NotNull String currency, final @NotNull String toCurrency) {
        Currency.isValidCurrency(currency);
        Currency.isValidCurrency(toCurrency);

        if (currency.equalsIgnoreCase(toCurrency)) {
            return 1D;
        }

        final var conversionRates = currencyExternalService
                .getCurrencyRatesFromApi(currency)
                .getConversionRates();

        if (!conversionRates.containsKey(toCurrency)) {
            throwBadRequest(invalidValue(toCurrency, CURRENCY, getAndSortKeys(conversionRates)));
        }

        return conversionRates.get(toCurrency);
    }

}
