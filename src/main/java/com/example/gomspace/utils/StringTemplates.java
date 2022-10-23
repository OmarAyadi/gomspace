package com.example.gomspace.utils;

public interface StringTemplates {

    static String invalidValue(final String value, final String type, final Iterable<String> possibleTypes) {
        return String.format("invalid %s value : %s, possible values %s", type, value, possibleTypes);
    }

    static String invalidAccountId(final Long id) {
        return String.format("invalid account id: %s", id);
    }

    static String invalidCurrency(final String currency) {
        return String.format("invalid currency: %s, not found in api call", currency);
    }

}
