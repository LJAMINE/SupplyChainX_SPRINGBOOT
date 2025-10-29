package org.example.supplychainx.common.exception;

public class RestExceptionHandler extends RuntimeException {
    public RestExceptionHandler(String message) {
        super(message);
    }
}
