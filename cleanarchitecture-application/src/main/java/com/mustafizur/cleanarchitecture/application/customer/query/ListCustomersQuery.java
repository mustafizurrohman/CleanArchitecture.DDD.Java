package com.mustafizur.cleanarchitecture.application.customer.query;

import com.mustafizur.cleanarchitecture.application.customer.CustomerView;
import com.mustafizur.cleanarchitecture.application.messaging.Query;
import com.mustafizur.cleanarchitecture.core.model.PageResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ListCustomersQuery(
        @Min(0) int page,
        @Min(1) @Max(200) int size) implements Query<PageResult<CustomerView>> {
}
