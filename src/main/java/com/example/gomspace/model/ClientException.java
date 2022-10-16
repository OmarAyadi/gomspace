package com.example.gomspace.model;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public final class ClientException extends ResponseStatusException {
    public ClientException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public static void throwBadRequest(String reason) throws ClientException {
        throw new ClientException(BAD_REQUEST, reason);
    }

    public static ClientException forbidden(String reason) {
        return new ClientException(FORBIDDEN, reason);
    }

    public static void handleValidationErrors(Errors errors) throws ClientException {

        List<ObjectError> errorList = errors.getAllErrors();

        if (errorList.isEmpty()) return;

        throwBadRequest(errorList.toString());
    }
}
