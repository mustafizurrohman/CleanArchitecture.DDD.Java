package com.mustafizur.cleanarchitecture.infrastructure.persistence;

import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerStatus;
import com.mustafizur.cleanarchitecture.domain.customer.Email;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
@SQLRestriction("deleted = false")
public class CustomerJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Convert(converter = EmailAttributeConverter.class)
    @Column(nullable = false, unique = true, length = 320)
    private Email email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private boolean deleted;

    @Version
    private long version;

    protected CustomerJpaEntity() {
    }

    private CustomerJpaEntity(Customer customer) {
        this.id = customer.id().value();
        this.createdAt = customer.createdAt();
        this.deleted = false;
        updateFrom(customer);
    }

    static CustomerJpaEntity from(Customer customer) {
        return new CustomerJpaEntity(customer);
    }

    void updateFrom(Customer customer) {
        this.name = customer.name();
        this.email = customer.email();
        this.status = customer.status();
        this.updatedAt = customer.updatedAt();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public long getVersion() {
        return version;
    }
}
