package com.mustafizur.cleanarchitecture.application.customer.query;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.messaging.Query;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetCustomerByIdQuery(@NotNull UUID customerId) implements Query<CustomerView> {
}
