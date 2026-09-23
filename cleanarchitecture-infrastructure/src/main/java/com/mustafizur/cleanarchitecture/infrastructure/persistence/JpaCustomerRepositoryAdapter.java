package com.mustafizur.cleanarchitecture.infrastructure.persistence;

import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.core.model.PageResult;
import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;
import com.mustafizur.cleanarchitecture.domain.customer.Email;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaCustomerRepositoryAdapter implements CustomerRepository {
    private final SpringDataCustomerRepository repository;

    public JpaCustomerRepositoryAdapter(SpringDataCustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return repository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public PageResult<Customer> findAll(int page, int size) {
        var result = repository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return new PageResult<>(
                result.getContent().stream().map(this::toDomain).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages());
    }

    @Override
    public Customer save(Customer customer) {
        // Reuse a managed/persisted entity for updates so the JPA @Version value is
        // preserved. Reconstructing a fresh entity on every update would reset the
        // version and break optimistic locking after the first modification.
        var entity = repository.findById(customer.id().value())
                .orElseGet(() -> CustomerJpaEntity.from(customer));
        entity.updateFrom(customer);
        repository.save(entity);
        return customer;
    }

    @Override
    public void softDelete(CustomerId id) {
        repository.softDeleteById(id.value());
    }

    private Customer toDomain(CustomerJpaEntity entity) {
        return Customer.rehydrate(
                new CustomerId(entity.getId()),
                entity.getName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
