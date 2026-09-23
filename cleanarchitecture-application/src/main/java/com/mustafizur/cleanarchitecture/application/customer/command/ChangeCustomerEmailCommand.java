package com.mustafizur.cleanarchitecture.application.customer.command;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.messaging.Command;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChangeCustomerEmailCommand(
        @NotNull UUID customerId,
        @NotBlank @Email @Size(max = 320) String email) implements Command<CustomerView> {
}
