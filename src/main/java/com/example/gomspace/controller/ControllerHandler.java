package com.example.gomspace.controller;

import com.example.gomspace.model.ClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.function.Function;

@ControllerAdvice
public class ControllerHandler extends ResponseEntityExceptionHandler {
    private static final Function<String, Map<String, Object>> generateErrorBody =
            (reason) -> Map.of("timestamp", Instant.now(), "message", reason);

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Object> handleClientException(ClientException ex, WebRequest request) {

        final var body = generateErrorBody.apply(ex.getReason());

        return new ResponseEntity<>(body, ex.getStatus());
    }

}
