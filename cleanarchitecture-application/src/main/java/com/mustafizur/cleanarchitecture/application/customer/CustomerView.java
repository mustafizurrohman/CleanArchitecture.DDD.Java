package com.mustafizur.cleanarchitecture.application.customer;

import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerView(
        UUID id,
        String name,
        String email,
        CustomerStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static CustomerView from(Customer customer) {
        return new CustomerView(
                customer.id().value(),
                customer.name(),
                customer.email().value(),
                customer.status(),
                customer.createdAt(),
                customer.updatedAt());
    }
}
