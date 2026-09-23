package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.command.DeleteCustomerCommand;
import com.mustafizur.cleanarchitecture.application.messaging.CommandHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.core.exception.NotFoundException;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

public final class DeleteCustomerHandler implements CommandHandler<DeleteCustomerCommand, Void> {
    private final CustomerRepository repository;

    public DeleteCustomerHandler(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Class<DeleteCustomerCommand> commandType() {
        return DeleteCustomerCommand.class;
    }

    @Override
    public Void handle(DeleteCustomerCommand command) {
        var id = new CustomerId(command.customerId());
        repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        repository.softDelete(id);
        return null;
    }
}
