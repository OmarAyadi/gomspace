package com.example.gomspace.service;


import com.example.gomspace.model.ClientException;
import com.example.gomspace.model.enums.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.gomspace.mock.CurrencyMock.VALID_CURRENCY_MOCK;
import static com.example.gomspace.mock.CurrencyMock.mockCurrencyDto;
import static com.example.gomspace.model.enums.Currency.EUR;
import static com.example.gomspace.model.enums.Currency.TND;
import static com.example.gomspace.utils.Constants.CURRENCY;
import static com.example.gomspace.utils.StringTemplates.invalidValue;
import static com.example.gomspace.utils.Utils.getAndSortKeys;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyExternalService currencyExternalService;

    @Nested
    class GetCurrencyRateTests {

        @DisplayName("throw error since currency provided is not a valid Currency enum")
        @Test
        public void throwErrorInvalidCurrency() {
            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> currencyService.getCurrencyRate("test", VALID_CURRENCY_MOCK));

            assertEquals(
                    invalidValue("test", CURRENCY, getAndSortKeys(Currency.possibleCurrencies)),
                    clientException.getReason()
            );
        }

        @DisplayName("throw error since toCurrency provided is not a valid Currency enum")
        @Test
        public void throwErrorInvalidToCurrency() {
            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> currencyService.getCurrencyRate(VALID_CURRENCY_MOCK, "test"));

            assertEquals(
                    invalidValue("test", CURRENCY, getAndSortKeys(Currency.possibleCurrencies)),
                    clientException.getReason()
            );
        }

        @DisplayName("throw error since toCurrency does not exists in the conversion rates of the currency")
        @Test
        public void throwErrorToCurrencyDoesNotExistsInExternalApi() {
            final var mockCurrencyExchange = mockCurrencyDto();

            when(currencyExternalService.getCurrencyRatesFromApi(VALID_CURRENCY_MOCK))
                    .thenReturn(mockCurrencyExchange);


            final var clientException =
                    assertThrows(
                            ClientException.class,
                            () -> currencyService.getCurrencyRate(VALID_CURRENCY_MOCK, TND.name()));

            assertEquals(
                    invalidValue(TND.name(), CURRENCY, getAndSortKeys(mockCurrencyExchange.getConversionRates())),
                    clientException.getReason()
            );

            verify(currencyExternalService, times(1))
                    .getCurrencyRatesFromApi(VALID_CURRENCY_MOCK);
        }

        @DisplayName("return 1 since currency == toCurrency")
        @Test
        public void returnIdentitySinceCurrenciesAreTheSame() {

            final var rate = currencyService.getCurrencyRate(VALID_CURRENCY_MOCK, VALID_CURRENCY_MOCK);

            assertEquals(1D, rate);

        }

        @DisplayName("return the corresponding rate from the currency exchange rates")
        @Test
        public void returnValidRate() {

            when(currencyExternalService.getCurrencyRatesFromApi(VALID_CURRENCY_MOCK))
                    .thenReturn(mockCurrencyDto());

            final var rate = currencyService.getCurrencyRate(VALID_CURRENCY_MOCK, EUR.name());

            assertEquals(3D, rate);

            verify(currencyExternalService, times(1))
                    .getCurrencyRatesFromApi(VALID_CURRENCY_MOCK);
        }
    }
}
