package com.mustafizur.cleanarchitecture.application.port.out;

import com.mustafizur.cleanarchitecture.core.model.PageResult;
import com.mustafizur.cleanarchitecture.domain.customer.Customer;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;
import com.mustafizur.cleanarchitecture.domain.customer.Email;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findById(CustomerId id);
    Optional<Customer> findByEmail(Email email);
    PageResult<Customer> findAll(int page, int size);
    Customer save(Customer customer);
    void softDelete(CustomerId id);
}
