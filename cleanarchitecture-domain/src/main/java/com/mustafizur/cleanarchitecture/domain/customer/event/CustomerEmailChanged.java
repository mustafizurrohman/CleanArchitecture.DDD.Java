package com.mustafizur.cleanarchitecture.domain.customer.event;

import com.mustafizur.cleanarchitecture.core.domain.DomainEvent;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

import java.time.Instant;
import java.util.UUID;

public record CustomerEmailChanged(
        UUID eventId,
        Instant occurredAt,
        CustomerId customerId,
        String oldEmail,
        String newEmail) implements DomainEvent {

    public static CustomerEmailChanged now(CustomerId customerId, String oldEmail, String newEmail) {
        return new CustomerEmailChanged(UUID.randomUUID(), Instant.now(), customerId, oldEmail, newEmail);
    }
}
