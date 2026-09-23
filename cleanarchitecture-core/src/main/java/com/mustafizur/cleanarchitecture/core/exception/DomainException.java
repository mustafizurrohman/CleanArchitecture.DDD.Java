package com.mustafizur.cleanarchitecture.core.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
