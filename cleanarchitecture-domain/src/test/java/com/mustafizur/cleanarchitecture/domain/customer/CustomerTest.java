package com.mustafizur.cleanarchitecture.domain.customer;

import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerCreated;
import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerDeactivated;
import com.mustafizur.cleanarchitecture.domain.customer.event.CustomerEmailChanged;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void createRaisesDomainEvent() {
        var customer = Customer.create("Ada Lovelace", Email.of("ADA@example.com"));

        assertThat(customer.email().value()).isEqualTo("ada@example.com");
        assertThat(customer.status()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.pullDomainEvents()).hasExactlyElementsOfTypes(CustomerCreated.class);
    }

    @Test
    void changingEmailAndDeactivatingRaiseEvents() {
        var customer = Customer.create("Ada Lovelace", Email.of("ada@example.com"));
        customer.pullDomainEvents();

        customer.changeEmail(Email.of("ada.lovelace@example.com"));
        customer.deactivate();

        assertThat(customer.pullDomainEvents())
                .hasExactlyElementsOfTypes(CustomerEmailChanged.class, CustomerDeactivated.class);
    }
}
