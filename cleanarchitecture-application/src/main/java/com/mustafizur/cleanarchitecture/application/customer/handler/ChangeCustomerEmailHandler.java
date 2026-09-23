package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.command.ChangeCustomerEmailCommand;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.application.port.out.DomainEventPublisher;
import com.mustafizur.cleanarchitecture.core.exception.ConflictException;
import com.mustafizur.cleanarchitecture.core.exception.NotFoundException;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;
import com.mustafizur.cleanarchitecture.domain.customer.Email;

public final class ChangeCustomerEmailHandler implements CommandHandler<ChangeCustomerEmailCommand, CustomerView> {
    private final CustomerRepository repository;
    private final DomainEventPublisher eventPublisher;

    public ChangeCustomerEmailHandler(CustomerRepository repository, DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Class<ChangeCustomerEmailCommand> commandType() {
        return ChangeCustomerEmailCommand.class;
    }

    @Override
    public CustomerView handle(ChangeCustomerEmailCommand command) {
        var id = new CustomerId(command.customerId());
        var customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        var newEmail = Email.of(command.email());
        repository.findByEmail(newEmail)
                .filter(other -> !other.id().equals(id))
                .ifPresent(other -> { throw new ConflictException("A customer with this email already exists"); });

        customer.changeEmail(newEmail);
        repository.save(customer);
        eventPublisher.publish(customer.pullDomainEvents());
        return CustomerView.from(customer);
    }
}
