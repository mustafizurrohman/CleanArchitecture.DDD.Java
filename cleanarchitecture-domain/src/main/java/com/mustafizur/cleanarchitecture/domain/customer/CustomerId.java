package com.mustafizur.cleanarchitecture.domain.customer;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId from(String value) {
        return new CustomerId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
