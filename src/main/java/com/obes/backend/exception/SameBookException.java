package com.obes.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class SameBookException extends RuntimeException {
    private static final long serialVersionUID = -8511277267511873591L;

    public SameBookException(String message) {
        super(message);
    }

    public SameBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
