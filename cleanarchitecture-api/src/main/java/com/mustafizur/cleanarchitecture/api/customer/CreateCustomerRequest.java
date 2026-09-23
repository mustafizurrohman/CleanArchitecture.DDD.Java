package com.mustafizur.cleanarchitecture.api.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 320) String email) {
}
