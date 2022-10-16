package com.example.gomspace.service;

import com.example.gomspace.dto.CurrencyDto;
import com.example.gomspace.model.ClientException;
import com.example.gomspace.model.enums.Currency;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.gomspace.utils.StringTemplates.invalidCurrency;

@Service
@AllArgsConstructor
public class CurrencyExternalService {
    private final RestTemplate restTemplate;

    @Cacheable(value = "currencies", key = "#currency")
    public CurrencyDto getCurrencyRatesFromApi(final String currency) {
        Currency.isValidCurrency(currency);

        final var url =
                String.format(
                        "https://v6.exchangerate-api.com/v6/ae8d2a31937643b85312c184/latest/%s",
                        currency
                );


        final var result = restTemplate.getForEntity(url, CurrencyDto.class);

        if (result.getStatusCode() != HttpStatus.OK) {
            ClientException.throwBadRequest(invalidCurrency(currency));
        }

        return result.getBody();
    }
}
