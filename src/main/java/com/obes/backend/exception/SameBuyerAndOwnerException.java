package com.obes.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class SameBuyerAndOwnerException extends RuntimeException {
    private static final long serialVersionUID = -8511277267511873591L;

    public SameBuyerAndOwnerException(String message) {
        super(message);
    }

    public SameBuyerAndOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
