package com.mustafizur.cleanarchitecture.application.customer.handler;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.customer.query.ListCustomersQuery;
import com.mustafizur.cleanarchitecture.application.messaging.QueryHandler;
import com.mustafizur.cleanarchitecture.application.port.out.CustomerRepository;
import com.mustafizur.cleanarchitecture.core.model.PageResult;

public final class ListCustomersHandler implements QueryHandler<ListCustomersQuery, PageResult<CustomerView>> {
    private final CustomerRepository repository;

    public ListCustomersHandler(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Class<ListCustomersQuery> queryType() {
        return ListCustomersQuery.class;
    }

    @Override
    public PageResult<CustomerView> handle(ListCustomersQuery query) {
        var page = repository.findAll(query.page(), query.size());
        return new PageResult<>(
                page.items().stream().map(CustomerView::from).toList(),
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages());
    }
}
