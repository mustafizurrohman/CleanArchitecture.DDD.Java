package com.mustafizur.cleanarchitecture.application.customer.command;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.messaging.Command;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerCommand(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 320) String email) implements Command<CustomerView> {
}
