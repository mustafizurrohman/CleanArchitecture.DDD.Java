package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.command.CreateCustomerCommand;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.application.port.out.DomainEventPublisher;
import com.mustafizur.cleanarchitecture.core.exception.ConflictException;
import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.Email;

public final class CreateCustomerHandler implements CommandHandler<CreateCustomerCommand, CustomerView> {
    private final CustomerRepository repository;
    private final DomainEventPublisher eventPublisher;

    public CreateCustomerHandler(CustomerRepository repository, DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Class<CreateCustomerCommand> commandType() {
        return CreateCustomerCommand.class;
    }

    @Override
    public CustomerView handle(CreateCustomerCommand command) {
        var email = Email.of(command.email());
        if (repository.findByEmail(email).isPresent()) {
            throw new ConflictException("A customer with this email already exists");
        }

        var customer = Customer.create(command.name(), email);
        repository.save(customer);
        eventPublisher.publish(customer.pullDomainEvents());
        return CustomerView.from(customer);
    }
}
