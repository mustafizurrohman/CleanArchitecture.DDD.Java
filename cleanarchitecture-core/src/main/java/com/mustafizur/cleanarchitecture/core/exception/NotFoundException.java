package com.mustafizur.cleanarchitecture.core.exception;

public final class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
