package com.mustafizur.cleanarchitecture.domain.customer;

import com.mustafizur.cleanarchitecture.core.domain.AggregateRoot;
import com.mustafizur.cleanarchitecture.core.exception.DomainException;
import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerCreated;
import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerDeactivated;
import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerEmailChanged;

import java.time.Instant;
import java.util.Objects;

public final class Customer extends AggregateRoot<CustomerId> {
    private String name;
    private Email email;
    private CustomerStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Customer(
            CustomerId id,
            String name,
            Email email,
            CustomerStatus status,
            Instant createdAt,
            Instant updatedAt) {
        super(id);
        this.name = normalizeName(name);
        this.email = Objects.requireNonNull(email);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Customer create(String name, Email email) {
        var now = Instant.now();
        var customer = new Customer(CustomerId.newId(), name, email, CustomerStatus.ACTIVE, now, now);
        customer.raise(CustomerCreated.now(customer.id(), customer.email.value()));
        return customer;
    }

    public static Customer rehydrate(
            CustomerId id,
            String name,
            Email email,
            CustomerStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Customer(id, name, email, status, createdAt, updatedAt);
    }

    public void rename(String newName) {
        this.name = normalizeName(newName);
        touch();
    }

    public void changeEmail(Email newEmail) {
        Objects.requireNonNull(newEmail);
        if (email.equals(newEmail)) {
            return;
        }
        var oldEmail = email;
        email = newEmail;
        touch();
        raise(CustomerEmailChanged.now(id(), oldEmail.value(), newEmail.value()));
    }

    public void deactivate() {
        if (status == CustomerStatus.INACTIVE) {
            return;
        }
        status = CustomerStatus.INACTIVE;
        touch();
        raise(CustomerDeactivated.now(id()));
    }

    public void activate() {
        if (status == CustomerStatus.ACTIVE) {
            return;
        }
        status = CustomerStatus.ACTIVE;
        touch();
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public CustomerStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    private void touch() {
        updatedAt = Instant.now();
    }

    private static String normalizeName(String value) {
        if (value == null || value.isBlank()) {
            throw new DomainException("Customer name must not be blank");
        }
        var normalized = value.strip().replaceAll("\\s+", " ");
        if (normalized.length() > 120) {
            throw new DomainException("Customer name must not exceed 120 characters");
        }
        return normalized;
    }
}
