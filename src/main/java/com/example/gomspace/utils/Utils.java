package com.example.gomspace.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Utils {

    static <T, R> List<T> getAndSortKeys(Map<T, R> map) {
        return map
                .keySet()
                .stream()
                .sorted()
                .collect(Collectors.toList());
    }

    static <T> Map<String, T> valuesToMap(T[] values) {
        return Arrays.stream(values).collect(
                Collectors.toMap(
                        Object::toString,
                        Function.identity()
                )
        );
    }
}
