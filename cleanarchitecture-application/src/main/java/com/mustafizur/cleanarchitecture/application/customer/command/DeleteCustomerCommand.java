package com.mustafizur.cleanarchitecture.application.customer.command;

import com.mustafizur.cleanarchitecture.application.messaging.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteCustomerCommand(@NotNull UUID customerId) implements Command<Void> {
}
