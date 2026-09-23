package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.query.GetCustomerByIdQuery;
import com.mustafizur.cleanarchitecture.application.messaging.QueryHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.core.exception.NotFoundException;
import com.mustafizur.cleanarchitecture.domain.customer.CustomerId;

public final class GetCustomerByIdHandler implements QueryHandler<GetCustomerByIdQuery, CustomerView> {
    private final CustomerRepository repository;

    public GetCustomerByIdHandler(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Class<GetCustomerByIdQuery> queryType() {
        return GetCustomerByIdQuery.class;
    }

    @Override
    public CustomerView handle(GetCustomerByIdQuery query) {
        var id = new CustomerId(query.customerId());
        return repository.findById(id)
                .map(CustomerView::from)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }
}
