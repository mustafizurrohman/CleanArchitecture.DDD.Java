package com.mustafizur.cleanarchitecture.domain.customer.event;

import com.mustafizur.cleanarchitecture.core.domain.DomainEvent;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

import java.time.Instant;
import java.util.UUID;

public record CustomerDeactivated(
        UUID eventId,
        Instant occurredAt,
        CustomerId customerId) implements DomainEvent {

    public static CustomerDeactivated now(CustomerId customerId) {
        return new CustomerDeactivated(UUID.randomUUID(), Instant.now(), customerId);
    }
}
