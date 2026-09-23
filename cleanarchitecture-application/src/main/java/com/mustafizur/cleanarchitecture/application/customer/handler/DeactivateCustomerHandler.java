package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.command.DeactivateCustomerCommand;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.application.port.out.DomainEventPublisher;
import com.mustafizur.cleanarchitecture.core.exception.NotFoundException;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

public final class DeactivateCustomerHandler implements CommandHandler<DeactivateCustomerCommand, CustomerView> {
    private final CustomerRepository repository;
    private final DomainEventPublisher eventPublisher;

    public DeactivateCustomerHandler(CustomerRepository repository, DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Class<DeactivateCustomerCommand> commandType() {
        return DeactivateCustomerCommand.class;
    }

    @Override
    public CustomerView handle(DeactivateCustomerCommand command) {
        var id = new CustomerId(command.customerId());
        var customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        customer.deactivate();
        repository.save(customer);
        eventPublisher.publish(customer.pullDomainEvents());
        return CustomerView.from(customer);
    }
}
