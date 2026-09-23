package com.mustafizur.cleanarchitecture.domain.customer.event;

import com.mustafizur.cleanarchitecture.core.domain.DomainEvent;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreated(
        UUID eventId,
        Instant occurredAt,
        CustomerId customerId,
        String email) implements DomainEvent {

    public static CustomerCreated now(CustomerId customerId, String email) {
        return new CustomerCreated(UUID.randomUUID(), Instant.now(), customerId, email);
    }
}
