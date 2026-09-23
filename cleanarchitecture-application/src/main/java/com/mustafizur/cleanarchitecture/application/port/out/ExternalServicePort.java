package com.mustafizur.cleanarchitecture.application.port.out;

public interface ExternalServicePort {
    ExternalServiceResult ping();

    record ExternalServiceResult(boolean successful, String source, String message) {
    }
}
